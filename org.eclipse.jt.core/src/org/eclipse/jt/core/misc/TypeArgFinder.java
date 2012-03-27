package org.eclipse.jt.core.misc;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * 类型参数查找器
 * 
 * @author Jeff Tang
 * 
 */
public final class TypeArgFinder {
	private TypeArgFinder() {

	}

	public static Class<?> tryToClass(Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		} else if (type instanceof GenericArrayType) {
			Class<?> cc = tryToClass(((GenericArrayType) type)
					.getGenericComponentType());
			if (cc != null) {
				return Array.newInstance(cc, 0).getClass();
			}
		} else if (type instanceof ParameterizedType) {
			return tryToClass(((ParameterizedType) type).getRawType());
		}
		return null;
	}

	private static UnsupportedOperationException buildException(
			Class<?> targetClass, Class<?> parameterizedClass, int index) {
		return new UnsupportedOperationException("无法确定范型类的第" + index + "个范型参数");
	}

	public static Class<?>[] get(Class<?> targetClass,
			Class<?> parameterizedClass) {
		ParameterizedType[] paramTypes = new ParameterizedType[8];
		int paramTypeCount = 0;
		for (; targetClass != parameterizedClass; targetClass = targetClass
				.getSuperclass()) {
			if (targetClass == Object.class) {
				throw new IllegalArgumentException("范型类不是目标类的父类型,无法提取类型参数");
			}
			Type t = targetClass.getGenericSuperclass();
			if (t instanceof ParameterizedType) {
				if (paramTypeCount == paramTypes.length) {
					ParameterizedType[] newParamTypes = new ParameterizedType[paramTypeCount * 2];
					System.arraycopy(paramTypes, 0, newParamTypes, 0,
							paramTypeCount);
					paramTypes = newParamTypes;
				}
				paramTypes[paramTypeCount++] = (ParameterizedType) t;
			}
		}
		if (paramTypeCount == 0) {
			throw new IllegalArgumentException("所给类型不是范型类");
		}
		int paramTypeIndex = paramTypeCount - 1;
		ParameterizedType pt = paramTypes[paramTypeIndex];

		if (pt.getRawType() != parameterizedClass) {
			throw new IllegalArgumentException("所给类型不是范型类");
		}
		Type[] typeArguments = pt.getActualTypeArguments();
		int argCount = typeArguments.length;
		Class<?>[] typeArgs = new Class[argCount];
		int[] typeArgIndexes = new int[argCount];
		for (int i = 0; i < argCount; i++) {
			typeArgIndexes[i] = i;
		}
		for (;;) {
			TypeVariable<?>[] tvs = null;
			for (int i = 0; i < typeArgIndexes.length; i++) {
				int argIndex = typeArgIndexes[i];
				if (argIndex < 0) {
					continue;
				}
				Type t = typeArguments[argIndex];
				if (t instanceof Class<?>) {
					typeArgs[i] = (Class<?>) t;
					typeArgIndexes[i] = -1;
					argCount--;
				} else if (t instanceof TypeVariable<?>) {
					if (tvs == null) {
						tvs = ((TypeVariable<?>) t).getGenericDeclaration()
								.getTypeParameters();
					}
					for (int j = 0; j < tvs.length; j++) {
						if (tvs[j] == t) {
							typeArgIndexes[i] = j;
							break;
						}
					}
				} else {
					if ((typeArgs[i] = tryToClass(t)) == null) {
						throw buildException(targetClass, parameterizedClass, i);
					}
					typeArgIndexes[i] = -1;
					argCount--;
				}
			}
			if (argCount == 0) {
				return typeArgs;
			} else if (paramTypeIndex == 0) {
				for (int i = 0; i < typeArgs.length; i++) {
					if (typeArgs[i] == null) {
						throw buildException(targetClass, parameterizedClass, i);
					}
				}
			} else {
				pt = paramTypes[--paramTypeIndex];
				typeArguments = pt.getActualTypeArguments();
			}
		}
	}

	public static Class<?> find(Class<?> targetClass,
			Class<?> parameterizedClass, int index) {
		return findOrGet(targetClass, parameterizedClass, index, true);
	}

	public static Class<?> get(Class<?> targetClass,
			Class<?> parameterizedClass, int index) {
		return findOrGet(targetClass, parameterizedClass, index, false);
	}

	private static Class<?> findOrGet(Class<?> targetClass,
			Class<?> parameterizedClass, int index, boolean find) {
		ParameterizedType[] paramTypes = new ParameterizedType[8];
		int paramTypeCount = 0;
		if (parameterizedClass.isInterface()) {
			Type[] gi;
			findPts: for (;;) {
				for (;;) {
					gi = targetClass.getGenericInterfaces();
					if (gi.length > 0) {
						break;
					}
					targetClass = targetClass.getSuperclass();
					if (targetClass == Object.class) {
						if (find) {
							return null;
						}
						throw new IllegalArgumentException(
								"范型类不是目标类的父类型,无法提取类型参数");
					}
				}
				fort: for (Type type : gi) {
					if (type instanceof ParameterizedType) {
						final ParameterizedType pt = (ParameterizedType) type;
						final Class<?> rawPt = (Class<?>) pt.getRawType();
						if (parameterizedClass.isAssignableFrom(rawPt)) {
							if (paramTypeCount == paramTypes.length) {
								ParameterizedType[] newParamTypes = new ParameterizedType[paramTypeCount * 2];
								System.arraycopy(paramTypes, 0, newParamTypes,
										0, paramTypeCount);
								paramTypes = newParamTypes;
							}
							paramTypes[paramTypeCount++] = pt;
							if (rawPt == parameterizedClass) {
								break findPts;
							}
							gi = rawPt.getGenericInterfaces();
							break fort;
						}
					}
				}
			}
		} else {
			for (; targetClass != parameterizedClass; targetClass = targetClass
					.getSuperclass()) {
				if (targetClass == Object.class) {
					if (find) {
						return null;
					}
					throw new IllegalArgumentException("范型类不是目标类的父类型,无法提取类型参数");
				}
				final Type t = targetClass.getGenericSuperclass();
				if (t instanceof ParameterizedType) {
					if (paramTypeCount == paramTypes.length) {
						ParameterizedType[] newParamTypes = new ParameterizedType[paramTypeCount * 2];
						System.arraycopy(paramTypes, 0, newParamTypes, 0,
								paramTypeCount);
						paramTypes = newParamTypes;
					}
					paramTypes[paramTypeCount++] = (ParameterizedType) t;
				}
			}
		}
		if (paramTypeCount == 0) {
			if (find) {
				return null;
			}
			throw new IllegalArgumentException("所给类型不是范型类");
		}
		int paramTypeIndex = paramTypeCount - 1;
		ParameterizedType pt = paramTypes[paramTypeIndex];

		if (pt.getRawType() != parameterizedClass) {
			if (find) {
				return null;
			}
			throw new IllegalArgumentException("所给类型不是范型类");
		}
		Type[] typeArguments = pt.getActualTypeArguments();
		int typeArgIndexe = index;
		for (;;) {
			Type t = typeArguments[typeArgIndexe];
			if (t instanceof Class<?>) {
				return (Class<?>) t;
			} else if (t instanceof TypeVariable<?>) {
				TypeVariable<?>[] tvs = ((TypeVariable<?>) t)
						.getGenericDeclaration().getTypeParameters();
				for (int j = 0; j < tvs.length; j++) {
					if (tvs[j] == t) {
						typeArgIndexe = j;
						break;
					}
				}
			} else {
				Class<?> c = tryToClass(t);
				if (c == null && !find) {
					throw buildException(targetClass, parameterizedClass, index);
				}
				return c;
			}
			if (paramTypeIndex == 0) {
				if (find) {
					return null;
				}
				throw buildException(targetClass, parameterizedClass, index);
			} else {
				pt = paramTypes[--paramTypeIndex];
				typeArguments = pt.getActualTypeArguments();
			}
		}
	}

	public static ParameterizedType findParameterizedType(Class<?> targetClass,
			Class<?> parameterizedClass, int index) {
		ParameterizedType[] paramTypes = new ParameterizedType[8];
		int paramTypeCount = 0;
		if (parameterizedClass.isInterface()) {
			Type[] gi;
			findPts: for (;;) {
				for (;;) {
					gi = targetClass.getGenericInterfaces();
					if (gi.length > 0) {
						break;
					}
					targetClass = targetClass.getSuperclass();
					if (targetClass == Object.class) {
						return null;
					}
				}
				fort: for (Type type : gi) {
					if (type instanceof ParameterizedType) {
						final ParameterizedType pt = (ParameterizedType) type;
						final Class<?> rawPt = (Class<?>) pt.getRawType();
						if (parameterizedClass.isAssignableFrom(rawPt)) {
							if (paramTypeCount == paramTypes.length) {
								ParameterizedType[] newParamTypes = new ParameterizedType[paramTypeCount * 2];
								System.arraycopy(paramTypes, 0, newParamTypes,
										0, paramTypeCount);
								paramTypes = newParamTypes;
							}
							paramTypes[paramTypeCount++] = pt;
							if (rawPt == parameterizedClass) {
								break findPts;
							}
							gi = rawPt.getGenericInterfaces();
							break fort;
						}
					}
				}
			}
		} else {
			for (; targetClass != parameterizedClass; targetClass = targetClass
					.getSuperclass()) {
				if (targetClass == Object.class) {
					return null;
				}
				final Type t = targetClass.getGenericSuperclass();
				if (t instanceof ParameterizedType) {
					if (paramTypeCount == paramTypes.length) {
						ParameterizedType[] newParamTypes = new ParameterizedType[paramTypeCount * 2];
						System.arraycopy(paramTypes, 0, newParamTypes, 0,
								paramTypeCount);
						paramTypes = newParamTypes;
					}
					paramTypes[paramTypeCount++] = (ParameterizedType) t;
				}
			}
		}
		if (paramTypeCount == 0) {
			return null;
		}
		int paramTypeIndex = paramTypeCount - 1;
		ParameterizedType pt = paramTypes[paramTypeIndex];

		if (pt.getRawType() != parameterizedClass) {
			return null;
		}
		Type[] typeArguments = pt.getActualTypeArguments();
		int typeArgIndexe = index;
		for (;;) {
			Type t = typeArguments[typeArgIndexe];
			if (t instanceof TypeVariable<?>) {
				TypeVariable<?>[] tvs = ((TypeVariable<?>) t)
						.getGenericDeclaration().getTypeParameters();
				for (int j = 0; j < tvs.length; j++) {
					if (tvs[j] == t) {
						typeArgIndexe = j;
						break;
					}
				}
			} else if (t instanceof ParameterizedType) {
				return (ParameterizedType) t;
			} else {
				return null;
			}
			if (paramTypeIndex == 0) {
				return null;
			} else {
				pt = paramTypes[--paramTypeIndex];
				typeArguments = pt.getActualTypeArguments();
			}
		}
	}

	@Deprecated
	public static Class<?> find(Class<?> ownerClass,
			ParameterizedType fieldType, int index) {
		Type t = fieldType.getActualTypeArguments()[index];
		if (t instanceof Class<?>) {
			return (Class<?>) t;
		} else if (t instanceof TypeVariable<?>) {
			GenericDeclaration gd = ((TypeVariable<?>) t)
					.getGenericDeclaration();
			if (gd instanceof Class<?>) {
				index = 0;
				for (TypeVariable<?> tv : gd.getTypeParameters()) {
					if (tv == t) {
						return findOrGet(ownerClass, (Class<?>) gd, index, true);
					}
					index++;
				}
			}
		}
		return null;
		// TODO
	}
}