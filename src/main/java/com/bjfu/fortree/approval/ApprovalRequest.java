package com.bjfu.fortree.approval;

/**
 * 可以进行申请审批的请求
 */
public interface ApprovalRequest {
    /**
     * 将申请请求转换成可以读懂的信息
     * @return 请求的信息
     */
    String toMessage();
}
