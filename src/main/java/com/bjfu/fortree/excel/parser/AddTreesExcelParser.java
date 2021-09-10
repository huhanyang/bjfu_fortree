package com.bjfu.fortree.excel.parser;

import com.alibaba.excel.EasyExcel;
import com.bjfu.fortree.excel.head.AddTreesHead;
import com.bjfu.fortree.pojo.request.woodland.AddTreesRequest;
import com.bjfu.fortree.spatial.G2dPoint;
import org.springframework.beans.BeanUtils;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数类解析
 *
 * @author warthog
 */
public class AddTreesExcelParser {

    public static List<AddTreesRequest.Tree> parse(InputStream inputStream) {
        List<AddTreesHead> list = EasyExcel.read(inputStream).head(AddTreesHead.class).sheet().doReadSync();
        return list.stream().map(addTreesHead -> {
            AddTreesRequest.Tree tree = new AddTreesRequest.Tree();
            BeanUtils.copyProperties(addTreesHead, tree);
            G2dPoint g2dPoint = new G2dPoint(addTreesHead.getLongitude(), addTreesHead.getLatitude());
            tree.setAbsolutePosition(g2dPoint);
            return tree;
        }).collect(Collectors.toList());
    }

}
