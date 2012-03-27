/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File PacketCode.java
 * Date 2009-3-9
 */
package org.eclipse.jt.core.impl;

/**
 * 数据包的代码。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// 改成注册式的？目前的处理方式不利于维护。
enum PacketCode {

    /**
     * 查询请求。
     */
    QUERY_REQUEST((byte) 0x11) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutRequest(dataPacket);
        }
    },

    /**
     * 任务处理请求。
     */
    TASK_REQUEST((byte) 0x12) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutRequest(dataPacket);
        }
    },

    /**
     * 结构定义请求。
     */
    STRUCT_REQUEST((byte) 0x13) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutRequest(dataPacket);
        }
    },

    /**
     * 事件广播请求。
     */
    EVENT_REQUEST((byte) 0x14) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutRequest(dataPacket);
        }
    },

    /**
     * 空返回。
     */
    VOID_RETURN((byte) 0x20) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutReturn(dataPacket);
        }
    },

    /**
     * 查询结果返回。
     */
    QUERY_RETURN((byte) 0x21) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutReturn(dataPacket);
        }
    },

    /**
     * 任务处理结果返回。
     */
    TASK_RETURN((byte) 0x22) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutReturn(dataPacket);
        }
    },

    /**
     * 结构定义请求结果返回。
     */
    STRUCT_RETURN((byte) 0x23) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutReturn(dataPacket);
        }
    },

    /**
     * 异常返回。
     */
    EXCEPTION_RETURN((byte) 0xF0) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.handOutException(dataPacket);
        }
    },

    /**
     * 强制取消处理器（例如因为出现异常不得不中止任务）。
     */
    FORCE_CANCEL_HANDLER((byte) 0xFA) {
        @Override
        void handle(NetConnection connection, DataPacket dataPacket) {
            connection.forceCancelHandler(dataPacket.requestId);
        }
    },

    /**
     * 强制取消请求存根（例如因为出现异常不得不中止任务）。
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

        // 这里理应用异常来报告错误，但效率代价太高，使用者会自行判断，故这里不用异常。
        // throw new IllegalArgumentException("No packet code " + code);
        return null;
    }

    abstract void handle(NetConnection connection, DataPacket dataPacket);
}
