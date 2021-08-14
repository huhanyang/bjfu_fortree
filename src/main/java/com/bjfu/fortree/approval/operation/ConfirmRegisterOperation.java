package com.bjfu.fortree.approval.operation;

import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.exception.BizException;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.User;
import com.bjfu.fortree.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 激活账号
 *
 * @author warthog
 */
@Component
public class ConfirmRegisterOperation implements ApprovedOperation {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
        String account = applyJob.getApplyParam();
        User user = userRepository.findByAccountForUpdate(account)
                .orElseThrow(() -> new BizException(ResultEnum.ACCOUNT_NOT_EXIST_OR_PASSWORD_WRONG));
        if (!user.getState().equals(UserStateEnum.UNACTIVE)) {
            throw new BizException(ResultEnum.ACCOUNT_NOT_UNACTIVE);
        }
        user.setState(UserStateEnum.ACTIVE);
        userRepository.save(user);
    }
}
