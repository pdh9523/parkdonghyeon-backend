package site.donghyeon.bank.presentation.common.controller.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.donghyeon.bank.presentation.common.resolver.CurrentUser;
import site.donghyeon.bank.presentation.common.resolver.GetClaims;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping()
    public CurrentUser test(@GetClaims CurrentUser currentUser) {
        return currentUser;
    }
}
