/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File PacketCode.java
 * Date 2009-3-9
 */
package org.eclipse.jt.core.impl;

/**
 * ���ݰ��Ĵ��롣
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// �ĳ�ע��ʽ�ģ�Ŀǰ�Ĵ���ʽ������ά����
enum PacketCode {

    /**
     * ��ѯ����
     */
    QUERY_REQUEST((byte) 0x11) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutRequest(dataPacket);
        }
    },

    /**
     * ����������
     */
    TASK_REQUEST((byte) 0x12) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutRequest(dataPacket);
        }
    },

    /**
     * �ṹ��������
     */
    STRUCT_REQUEST((byte) 0x13) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutRequest(dataPacket);
        }
    },

    /**
     * �¼��㲥����
     */
    EVENT_REQUEST((byte) 0x14) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutRequest(dataPacket);
        }
    },

    /**
     * �շ��ء�
     */
    VOID_RETURN((byte) 0x20) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutReturn(dataPacket);
        }
    },

    /**
     * ��ѯ������ء�
     */
    QUERY_RETURN((byte) 0x21) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutReturn(dataPacket);
        }
    },

    /**
     * �����������ء�
     */
    TASK_RETURN((byte) 0x22) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutReturn(dataPacket);
        }
    },

    /**
     * �ṹ�������������ء�
     */
    STRUCT_RETURN((byte) 0x23) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutReturn(dataPacket);
        }
    },

    /**
     * �쳣���ء�
     */
    EXCEPTION_RETURN((byte) 0xF0) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutException(dataPacket);
        }
    },

    /**
     * ǿ��ȡ����������������Ϊ�����쳣���ò���ֹ���񣩡�
     */
    FORCE_CANCEL_HANDLER((byte) 0xFA) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.forceCancelHandler(dataPacket.requestId);
        }
    },

    /**
     * ǿ��ȡ����������������Ϊ�����쳣���ò���ֹ���񣩡�
     */
    FORCE_CANCEL_STUB((byte) 0xFB) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.forceCancelStub(dataPacket);
        }
    },

    /*------------------------------------------------------------------------*
     * Cluster Relative
     *------------------------------------------------------------------------*/

    CLUSTER_LOCK_REQUEST((byte) 0xC1) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            // TODO Auto-generated method stub

        }
    },

    CLUSTER_LOCK_RETURN((byte) 0xC2) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            // TODO Auto-generated method stub

        }
    },

    CLUSTER_DATA_REQUEST((byte) 0xC3) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            // TODO Auto-generated method stub

        }
    },

    CLUSTER_DATA_RETURN((byte) 0xC4) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            // TODO Auto-generated method stub

        }
    },
    ;

    // /////////////////////////////////////////////////////////////////////////

    final byte code;

    private PacketCode(byte code) {
        this.code = code;
    }

    static PacketCode valueOf(byte code) {
        PacketCode[] codes = PacketCode.values();
        for (int i = 0, len = codes.length; i < len; i++) {
            if (codes[i].code == code) {
                return codes[i];
            }
        }

        // ������Ӧ���쳣��������󣬵�Ч�ʴ���̫�ߣ�ʹ���߻������жϣ������ﲻ���쳣��
        // throw new IllegalArgumentException("No packet code " + code);
        return null;
    }

    abstract void handle(NetConnection connection, DataPacket dataPacket);
}
