package com.bjfu.fortree.controller;

import com.bjfu.fortree.dto.job.ApplyJobDTO;
import com.bjfu.fortree.dto.woodland.WoodlandDTO;
import com.bjfu.fortree.dto.woodland.WoodlandDetailDTO;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.request.woodland.*;
import com.bjfu.fortree.security.annotation.RequireLogin;
import com.bjfu.fortree.service.WoodlandService;
import com.bjfu.fortree.util.SessionUtil;
import com.bjfu.fortree.vo.BaseResult;
import com.bjfu.fortree.vo.PageVO;
import com.bjfu.fortree.vo.apply.ApplyJobVO;
import com.bjfu.fortree.vo.woodland.WoodlandDetailVO;
import com.bjfu.fortree.vo.woodland.WoodlandVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 林地相关操作接口
 * @author warthog
 */
@RestController
@RequestMapping("/woodland")
@Validated
public class WoodlandController {

    @Autowired
    private WoodlandService woodlandService;

    @RequireLogin
    @PutMapping("/createWoodland")
    public BaseResult<ApplyJobVO> createWoodland(@Validated @RequestBody CreateWoodlandRequest createWoodlandRequest, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = woodlandService.createWoodland(userAccount, createWoodlandRequest);
        ApplyJobVO applyJobVO = new ApplyJobVO(applyJobDTO);
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobVO);
    }

    @RequireLogin
    @PutMapping("/addRecord")
    public BaseResult<ApplyJobVO> addRecord(@Validated @RequestBody AddRecordRequest addRecordRequest, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = woodlandService.addRecord(userAccount, addRecordRequest);
        ApplyJobVO applyJobVO = new ApplyJobVO(applyJobDTO);
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobVO);
    }

    @RequireLogin
    @PutMapping("/addTrees")
    public BaseResult<ApplyJobVO> addTrees(@Validated @RequestBody AddTreesRequest addTreesRequest, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = woodlandService.addTrees(userAccount, addTreesRequest);
        ApplyJobVO applyJobVO = new ApplyJobVO(applyJobDTO);
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobVO);
    }

    @RequireLogin
    @DeleteMapping("/deleteWoodland")
    public BaseResult<ApplyJobVO> deleteWoodland(@NotNull(message = "林地id不能为空!") Long id, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = woodlandService.deleteWoodland(userAccount, id);
        ApplyJobVO applyJobVO = new ApplyJobVO(applyJobDTO);
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobVO);
    }

    @RequireLogin
    @DeleteMapping("/deleteRecord")
    public BaseResult<ApplyJobVO> deleteRecord(@NotNull(message = "林地记录id不能为空!") Long recordId, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = woodlandService.deleteRecord(userAccount, recordId);
        ApplyJobVO applyJobVO = new ApplyJobVO(applyJobDTO);
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobVO);
    }

    @RequireLogin
    @DeleteMapping("/deleteTrees")
    public BaseResult<ApplyJobVO> deleteTrees(@Validated @RequestBody DeleteTreesRequest deleteTreesRequest, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = woodlandService.deleteTrees(userAccount, deleteTreesRequest);
        ApplyJobVO applyJobVO = new ApplyJobVO(applyJobDTO);
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobVO);
    }

    @RequireLogin
    @PostMapping("/editWoodland")
    public BaseResult<ApplyJobVO> editWoodland(@Validated @RequestBody EditWoodlandRequest editWoodlandRequest, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = woodlandService.editWoodland(userAccount, editWoodlandRequest);
        ApplyJobVO applyJobVO = new ApplyJobVO(applyJobDTO);
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobVO);
    }

    @RequireLogin
    @PostMapping("/editRecord")
    public BaseResult<ApplyJobVO> editRecord(@Validated @RequestBody EditRecordRequest editRecordRequest, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = woodlandService.editRecord(userAccount, editRecordRequest);
        ApplyJobVO applyJobVO = new ApplyJobVO(applyJobDTO);
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobVO);
    }

    @RequireLogin
    @PostMapping("/getWoodlandByCreator")
    public BaseResult<PageVO<WoodlandVO>> getWoodlandByCreator(@Validated @RequestBody GetWoodlandByCreatorRequest getWoodlandByCreatorRequest, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        PageVO<WoodlandDTO> woodlandDTOPageVO= woodlandService.getWoodlandByCreator(userAccount, getWoodlandByCreatorRequest);
        List<WoodlandVO> woodlandVOS = woodlandDTOPageVO.getContents().stream().map(WoodlandVO::new).collect(Collectors.toList());
        return new BaseResult<>(ResultEnum.SUCCESS, new PageVO<>(woodlandDTOPageVO.getCount(), woodlandVOS));
    }

    @RequireLogin
    @GetMapping("/getWoodlandDetail")
    public BaseResult<WoodlandDetailVO> getWoodlandDetail(@NotNull(message = "林地id不能为空!") Long id) {
        WoodlandDetailDTO woodlandDetail = woodlandService.getWoodlandDetail(id);
        return new BaseResult<>(ResultEnum.SUCCESS, new WoodlandDetailVO(woodlandDetail));
    }

}
