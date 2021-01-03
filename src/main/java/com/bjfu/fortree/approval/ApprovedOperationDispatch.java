package com.bjfu.fortree.approval;

import com.bjfu.fortree.approval.operation.ApprovedOperation;
import com.bjfu.fortree.entity.apply.ApplyJob;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.exception.ForTreeException;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.repository.job.ApplyJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 申请审批通过后的操作器分发器
 * @author warthog
 */
@Component
@Slf4j
public class ApprovedOperationDispatch {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ApplyJobRepository applyJobRepository;

    /**
     * 根据类型分发执行器并执行
     * @param applyJob 通过的申请实体
     */
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public void dispatch(ApplyJob applyJob) {
        // 记操作器是否录执行成功
        boolean operateSuccess = true;
        String errorMsg = "";
        // 更新并对申请加锁
        Optional<ApplyJob> applyJobOptional = applyJobRepository.findByIdForUpdate(applyJob.getId());
        if(applyJobOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.UNKNOWN_ERROR);
        }
        applyJob = applyJobOptional.get();
        // 从枚举类中获取操作器
        Class<ApprovedOperation> approvedOperationClass = applyJob.getType().getApprovedOperationClass();
        ApprovedOperation approvedOperation = applicationContext.getBean(approvedOperationClass);
        try{
            log.debug("开始执行审批通过后的操作器 操作器类型:{}", approvedOperation.getClass().getTypeName());
            approvedOperation.execute(applyJob.getApplyParam(), applyJob.getApplyUser());
        } catch (ForTreeException forTreeException) {
            operateSuccess = false;
            errorMsg = forTreeException.getResultEnum().getMsg();
            log.debug("审批通过后的操作器执行失败 业务异常信息:{}", errorMsg);
        } catch (Exception exception) {
            operateSuccess = false;
            errorMsg = exception.getMessage();
            log.error("审批通过后的操作器执行失败", exception);
        }
        if(!operateSuccess) {
            // 如果操作器执行失败更新申请的状态为通过但执行失败
            applyJob.setState(ApplyJobStateEnum.PASSED_EXECUTION_FAILED);
            applyJob.setMsg(errorMsg);
            applyJobRepository.save(applyJob);
        }
    }
}