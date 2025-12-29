package site.donghyeon.bank.presentation.account.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.donghyeon.bank.application.account.AccountTransactionUseCase;
import site.donghyeon.bank.application.account.AccountUseCase;
import site.donghyeon.bank.application.account.AccountOperationUseCase;
import site.donghyeon.bank.presentation.account.request.*;
import site.donghyeon.bank.presentation.account.response.*;
import site.donghyeon.bank.presentation.common.resolver.CurrentUser;
import site.donghyeon.bank.presentation.common.resolver.GetClaims;

import java.util.UUID;


@RestController
@RequestMapping("/account")
@Tag(name = "계좌", description = "계좌 관련 API 입니다.")
public class AccountController {

    private final AccountUseCase accountUseCase;
    private final AccountOperationUseCase accountOperationUseCase;
    private final AccountTransactionUseCase accountTransactionUseCase;

    public AccountController(
            AccountUseCase accountUseCase,
            AccountOperationUseCase accountOperationUseCase,
            AccountTransactionUseCase accountTransactionUseCase
    ) {
        this.accountUseCase = accountUseCase;
        this.accountOperationUseCase = accountOperationUseCase;
        this.accountTransactionUseCase = accountTransactionUseCase;
    }

    @GetMapping()
    @Operation(
            summary = "계좌 조회",
            description = "<p>내가 가진 모든 계좌를 조회합니다.</p>"
    )
    public ResponseEntity<MyAccountsResponse> getMyAccounts(
            @Parameter(hidden = true)
            @GetClaims CurrentUser currentUser
    ) {
        return ResponseEntity.ok(
                MyAccountsResponse.from(
                    accountUseCase.getMyAccounts(MyAccountsRequest.from(currentUser.userId()).toQuery())
                )
        );
    }

    @PostMapping()
    @Operation(
            summary = "계좌 개설",
            description = "<p>계좌를 개설합니다.</p>"
    )
    public ResponseEntity<OpenAccountResponse> openAccount(
            @Parameter(hidden = true)
            @GetClaims CurrentUser currentUser
    ) {
        return ResponseEntity.ok(
                OpenAccountResponse.from(
                        accountUseCase.openAccount(OpenAccountRequest.from(currentUser).toCommand())
                )
        );
    }

    @DeleteMapping()
    @Operation(
            summary = "계좌 해지",
            description = "<p>계좌를 해지합니다.</p>"
    )
    public ResponseEntity<Void> closeAccount(
            @Parameter(hidden = true)
            @GetClaims CurrentUser currentUser,
            @RequestBody CloseAccountRequest request
    ) {
        accountUseCase.closeAccount(request.toCommand(currentUser.userId()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{accountId}/deposit")
    @Operation(
            summary = "계좌 입금",
            description = "<p>계좌에 금액을 입금합니다.</p>" +
                    "<p> 입금에 성공한 경우, 거래 내역의 PK를 반환합니다. </p>" +
                    "<p>TODO: 무통장 입금 등을 고려했을 때 잔고 표시 여부에 대한 논의</p>"
    )
    public ResponseEntity<DepositResponse> deposit(
            @PathVariable UUID accountId,
            @RequestBody DepositRequest request
    ) {
        return ResponseEntity.accepted().body(
                DepositResponse.from(
                    accountOperationUseCase.deposit(request.toCommand(accountId))
                )
        );
    }

    @PostMapping("/{accountId}/withdrawal")
    @Operation(
            summary = "계좌 출금",
            description = "<p>계좌에서 금액을 출금합니다.</p>" +
                    "<p> 출금에 성공한 경우, 거래 내역의 PK를 반환합니다. </p>"
    )
    public ResponseEntity<WithdrawalResponse> withdrawal(
            @Parameter(hidden = true)
            @GetClaims CurrentUser currentUser,
            @PathVariable UUID accountId,
            @RequestBody WithdrawalRequest request
    ) {
        return ResponseEntity.accepted().body(
                WithdrawalResponse.from(
                        accountOperationUseCase.withdrawal(request.toCommand(currentUser.userId(), accountId))
                )
        );
    }

    @PostMapping("/{accountId}/transfer")
    @Operation(
            summary = "계좌 이체",
            description = "<p>내 계좌에서 상대 계좌로 금액을 이체합니다.</p>" +
                    "<p> 이체에 성공한 경우, 거래 내역의 PK를 반환합니다. </p>"
    )
    public ResponseEntity<TransferResponse> transfer(
            @Parameter(hidden = true)
            @GetClaims CurrentUser currentUser,
            @PathVariable UUID accountId,
            @RequestBody TransferRequest request
    ) {
        return ResponseEntity.accepted().body(
                TransferResponse.from(
                        accountOperationUseCase.transfer(request.toCommand(currentUser.userId(), accountId))
                )
        );
    }

    @GetMapping("/{accountId}/transactions")
    @Operation(
            summary = "거래 내역 조회",
            description = "<p>계좌의 거래 내역을 조회합니다.</p>"
    )
    public ResponseEntity<TransactionsResponse> getTransactions(
            @Parameter(hidden = true)
            @GetClaims CurrentUser currentUser,
            @PathVariable UUID accountId,
            @ModelAttribute TransactionsRequest request
    ) {
        return ResponseEntity.ok(
                TransactionsResponse.from(
                        accountTransactionUseCase.getTransactions(
                                request.toQuery(currentUser.userId(), accountId)
                        )
                )
        );
    }
}
