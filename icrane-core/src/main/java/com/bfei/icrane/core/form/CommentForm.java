package com.bfei.icrane.core.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * Created by moying on 2018/7/18.
 */
@Data
public class CommentForm {

    @NotEmpty
    private Integer memberId;

    @NotEmpty
    private String token;

    @NotEmpty
    @Length(max = 10)
    private String comment;

    @NotEmpty
    private Integer doll;
}
