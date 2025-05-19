package com.example.yz.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yz.server.entity.Family;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FamilyMapper extends BaseMapper<Family> {
    @Select("SELECT familyID FROM families WHERE AdminID = #{adminID}")
    Integer findFamilyIdByAdminId(@Param("adminID") int adminID);

}
