package com.example.yz.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yz.server.entity.PurchaseImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PurchaseImageMapper extends BaseMapper<PurchaseImage> {
    @Select("SELECT * FROM purchase_images WHERE RequestID = #{requestId}")
    List<PurchaseImage> selectByRequestId(Integer requestId);
}
