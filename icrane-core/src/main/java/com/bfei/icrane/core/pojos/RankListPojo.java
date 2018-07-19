package com.bfei.icrane.core.pojos;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by moying on 2018/7/18.
 */
@Data
public class RankListPojo implements Serializable {
    private static final long serialVersionUID = 4059290184993368848L;
    private Rankpojo rankpojo;
    private List<Rankpojo> rankpojos;
}
