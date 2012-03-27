package org.eclipse.jt.core.auth;

import org.eclipse.jt.core.User;

/**
 * �û���Դ����<br>
 * ��ö�����ж������ܹ����û���Դ���еĲ�����
 * 
 * <pre>
 * ACCESS:             ���ʲ���
 * UPDATE_BASE_INFO:   �޸Ļ�����Ϣ����
 * UPDATE_PASSWORD:    �޸��������
 * UPDATE_ROLE_ASSIGN: �޸Ľ�ɫ�������
 * UPDATE_AUTHORITY:   �޸���Ȩ����
 * DELETE:             ɾ������
 * </pre>
 * 
 * @see org.eclipse.jt.core.auth.Operation
 * @author Jeff Tang 2009-12
 */
public enum UserOperation implements Operation<User> {

	/**
	 * ���ʲ���
	 */
	ACCESS {
		public int getMask() {
			return 1 << 0;
		}

		public String getTitle() {
			return "����";
		}
	}
	// ,
	//
	// /**
	// * �޸Ļ�����Ϣ����
	// */
	// UPDATE_BASE_INFO {
	// public int getMask() {
	// return 1 << 1;
	// }
	//
	// public String getTitle() {
	// return "�޸Ļ�����Ϣ";
	// }
	// },
	//
	// /**
	// * �޸��������
	// */
	// UPDATE_PASSWORD {
	// public int getMask() {
	// return 1 << 2;
	// }
	//
	// public String getTitle() {
	// return "�޸�����";
	// }
	// },
	//
	// /**
	// * �޸Ľ�ɫ�������
	// */
	// UPDATE_ROLE_ASSIGN {
	// public int getMask() {
	// return 1 << 3;
	// }
	//
	// public String getTitle() {
	// return "�޸Ľ�ɫ����";
	// }
	// },
	//
	// /**
	// * �޸�Ȩ�޲���
	// */
	// UPDATE_AUTHORITY {
	// public int getMask() {
	// return 1 << 4;
	// }
	//
	// public String getTitle() {
	// return "�޸�Ȩ��";
	// }
	// },
	//
	// /**
	// * ɾ������
	// */
	// DELETE {
	// public int getMask() {
	// return 1 << 5;
	// }
	//
	// public String getTitle() {
	// return "ɾ��";
	// }
	// }

}
