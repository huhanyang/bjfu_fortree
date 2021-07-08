package com.bjfu.fortree.controller;

import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.pojo.dto.ApplyJobDTO;
import com.bjfu.fortree.pojo.dto.UserDTO;
import com.bjfu.fortree.pojo.dto.TreeDTO;
import com.bjfu.fortree.pojo.dto.WoodlandDTO;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.pojo.request.woodland.*;
import com.bjfu.fortree.pojo.vo.ApplyJobVO;
import com.bjfu.fortree.pojo.vo.TreeVO;
import com.bjfu.fortree.pojo.vo.WoodlandVO;
import com.bjfu.fortree.security.annotation.RequireLogin;
import com.bjfu.fortree.service.WoodlandService;
import com.bjfu.fortree.pojo.BaseResult;
import com.bjfu.fortree.util.UserInfoContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 林地相关操作接口
 * @author warthog
 */
@Validated
@RestController
@RequestMapping("/woodland")
public class WoodlandController {

    @Autowired
    private WoodlandService woodlandService;

    @RequireLogin
    @PostMapping("/createWoodland")
    public BaseResult<ApplyJobVO> createWoodland(@Validated @RequestBody CreateWoodlandRequest request) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDTO = woodlandService.createWoodland(userInfo.getAccount(), request);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @PostMapping("/addRecord")
    public BaseResult<ApplyJobVO> addRecord(@Validated @RequestBody AddRecordRequest request) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDTO = woodlandService.addRecord(userInfo.getAccount(), request);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @PostMapping("/addTrees")
    public BaseResult<ApplyJobVO> addTrees(@Validated @RequestBody AddTreesRequest request) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDTO = woodlandService.addTrees(userInfo.getAccount(), request);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @PostMapping("/addTreesByExcel")
    public BaseResult<ApplyJobVO> addTreesByExcel(AddTreesByExcelRequest request) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDTO = woodlandService.addTreesByExcel(userInfo.getAccount(), request);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @DeleteMapping("/deleteWoodland")
    public BaseResult<ApplyJobVO> deleteWoodland(@NotNull(message = "林地id不能为空!") Long id) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDTO = woodlandService.deleteWoodland(userInfo.getAccount(), id);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @DeleteMapping("/deleteRecord")
    public BaseResult<ApplyJobVO> deleteRecord(@NotNull(message = "林地记录id不能为空!") Long id) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDTO = woodlandService.deleteRecord(userInfo.getAccount(), id);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @PostMapping("/deleteTrees")
    public BaseResult<ApplyJobVO> deleteTrees(@Validated @RequestBody DeleteTreesRequest request) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDTO = woodlandService.deleteTrees(userInfo.getAccount(), request);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @PostMapping("/editWoodland")
    public BaseResult<ApplyJobVO> editWoodland(@Validated @RequestBody EditWoodlandRequest request) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDTO = woodlandService.editWoodland(userInfo.getAccount(), request);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @PostMapping("/editRecord")
    public BaseResult<ApplyJobVO> editRecord(@Validated @RequestBody EditRecordRequest request) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDTO = woodlandService.editRecord(userInfo.getAccount(), request);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @PostMapping("/getWoodlands")
    public BaseResult<Page<WoodlandVO>> getWoodlands(@Validated @RequestBody GetWoodlandsRequest request) {
        Page<WoodlandDTO> woodlandDTOS= woodlandService.getWoodlands(request);
        return new BaseResult<>(ResultEnum.SUCCESS, woodlandDTOS.map(WoodlandVO::new));
    }

    @GetMapping("/getAllWoodlands")
    public BaseResult<List<WoodlandVO>> getAllWoodlands() {
        List<WoodlandDTO> woodlandDTOS = woodlandService.getAllWoodlands();
        List<WoodlandVO> woodlandVOS = woodlandDTOS.stream().map(WoodlandVO::new).collect(Collectors.toList());
        return new BaseResult<>(ResultEnum.SUCCESS, woodlandVOS);
    }

    @PostMapping("/getAllWoodlands")
    public BaseResult<List<WoodlandVO>> getAllWoodlands(@Validated @RequestBody GetAllWoodlandsRequest request) {
        List<WoodlandDTO> woodlandDTOS = woodlandService.getAllWoodlands(request);
        List<WoodlandVO> woodlandVOS = woodlandDTOS.stream().map(WoodlandVO::new).collect(Collectors.toList());
        return new BaseResult<>(ResultEnum.SUCCESS, woodlandVOS);
    }

    @RequireLogin
    @PostMapping("/getWoodlandsByCreator")
    public BaseResult<Page<WoodlandVO>> getWoodlandsByCreator(@Validated @RequestBody GetWoodlandsRequest request) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        Page<WoodlandDTO> woodlandDTOS= woodlandService.getWoodlandsByCreator(userInfo.getAccount(), request);
        return new BaseResult<>(ResultEnum.SUCCESS, woodlandDTOS.map(WoodlandVO::new));
    }

    @GetMapping("/getWoodlandDetail")
    public BaseResult<WoodlandVO> getWoodlandDetail(@NotNull(message = "林地id不能为空!") Long id) {
        WoodlandDTO woodlandDetail = woodlandService.getWoodlandDetail(id);
        return new BaseResult<>(ResultEnum.SUCCESS, new WoodlandVO(woodlandDetail));
    }

    @RequireLogin
    @PostMapping("/getTrees")
    public BaseResult<Page<TreeVO>> getTrees(@Validated @RequestBody GetTreesRequest request) {
        Page<TreeDTO> trees = woodlandService.getTrees(request);
        return new BaseResult<>(ResultEnum.SUCCESS, trees.map(TreeVO::new));
    }

}
