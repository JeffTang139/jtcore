package org.eclipse.jt.core.auth;

/**
 * ��ɫ��Դ����<br>
 * ��ö�����ж������ܹ��Խ�ɫ��Դ���еĲ�����
 * 
 * <pre>
 * ACCESS:           ���ʲ���
 * UPDATE_BASE_INFO: �޸Ļ�����Ϣ����
 * UPDATE_AUTHORITY: �޸���Ȩ����
 * DELETE:           ɾ������
 * ASSIGN:           �������
 * </pre>
 * 
 * @see org.eclipse.jt.core.auth.Operation
 * @author Jeff Tang 2009-12
 */
public enum RoleOperation implements Operation<Role> {

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
	},

	MODIFY {
		public int getMask() {
			return 1 << 2;
		}

		public String getTitle() {
			return "�޸�";
		}
	},

	DELETE {
		public int getMask() {
			return 1 << 3;
		}

		public String getTitle() {
			return "ɾ��";
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
	// return "�޸���Ϣ";
	// }
	// },
	//	
	// /**
	// * �޸�Ȩ�޲���
	// */
	// UPDATE_AUTHORITY {
	// public int getMask() {
	// return 1 << 2;
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
	// return 1 << 3;
	// }
	//
	// public String getTitle() {
	// return "ɾ��";
	// }
	// },
	//	
	// /**
	// * �������
	// */
	// ASSIGN {
	// public int getMask() {
	// return 1 << 4;
	// }
	//
	// public String getTitle() {
	// return "����";
	// }
	// }

}
