package com.bfei.icrane.core.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;


/**
 * Created by moying on 2018/7/18.
 */
@Data
public class CommentForm {

    @NotNull
    private Integer memberId;

    @NotEmpty
    private String token;

    @NotEmpty
    private String comment;

    @NotNull
    private Integer doll;
}
