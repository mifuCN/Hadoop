package com.mifu.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mifu.usercenter.common.BaseResponse;
import com.mifu.usercenter.common.ErrorCode;
import com.mifu.usercenter.common.ResultUtils;
import com.mifu.usercenter.exception.BusinessException;
import com.mifu.usercenter.model.domain.User;
import com.mifu.usercenter.model.domain.request.UserLoginRequest;
import com.mifu.usercenter.model.domain.request.UserRegisterRequest;
import com.mifu.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.mifu.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.mifu.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * @RestController 适用于编写restful风格的api, 返回值默认为 json 类型
 */

/**
 * 用户接口
 *
 * @author mifuRD
 */
@RestController
@RequestMapping("/user")
public class UserContorller {
    @Resource //Spring中的注解 可以将UserService引入到当前这个类里面
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {//@RequestBody注解:SpringMVC才知道和前端的json参数对应
        if (userRegisterRequest == null) {
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);//这个方法不太好 先去定义一个自定义异常类BussinessException
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        /**
         * 原先在service层已经校验过一次了  这里为什么需要再次校验一次呢
         * controller层 倾向于对请求参数本身的校验，不涉及业务逻辑本身(越少越好)
         * service层 是对业务逻辑的校验（有可能被controller之外的类调用)
         */
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {//@RequestBody注解:SpringMVC才知道和前端的json参数对应
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        // 鉴权 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE); //获取用户的登录态
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User getsafetyUser = userService.getsafetyUser(user);
        return ResultUtils.success(getsafetyUser);
    }


    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {//apache中的工具类 用来判断是否为空
            /**
             * 这里的queryWrapper.like方法这样写就是模糊查询
             * 追到源码 看到了这个
             *             default:
             *                 return "%" + str + "%";
             */
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        /**
         * 这里将userList转换为一个数据流，然后遍历userList中的每一个元素，把他的每个元素的密码设置为null,再把这些拼成一个完整的List
         */
        List<User> list = userList.stream().map(user -> userService.getsafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @GetMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);//MybatisPlus框架的逻辑删除 是框架自动转换的
        return ResultUtils.success(b);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 鉴权 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
