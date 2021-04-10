package com.ztl.gym.code.mapper;

import com.ztl.gym.code.domain.Code;

import java.util.List;
import java.util.Map;

public interface CodeMapper {
    Code selectCodeById( Map<String, Object> params);

    Code selectCode( Map<String, Object> params);

    List<Code> selectCodeListByCompanyId(long companyId);

    int insertCode(Code code);
}
