package com.bootcamp.paymentservice.iyzico;

import com.bootcamp.paymentservice.event.PaymentRequestEvent;
import com.iyzipay.Options;
import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.Currency;
import com.iyzipay.model.Locale;
import com.iyzipay.model.PaymentChannel;
import com.iyzipay.model.PaymentGroup;
import com.iyzipay.request.CreatePaymentRequest;
import com.iyzipay.model.PaymentCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IyzicoPaymentClient {

    private static final Logger log = LoggerFactory.getLogger(IyzicoPaymentClient.class);

    private final Options options;

    public IyzicoPaymentClient(Options options) {
        this.options = options;
    }

    public IyzicoResult charge(PaymentRequestEvent request, String conversationId) {
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
        paymentRequest.setLocale(Locale.TR.getValue());
        paymentRequest.setConversationId(conversationId);
        paymentRequest.setPrice(request.getAmount());
        paymentRequest.setPaidPrice(request.getAmount());
        paymentRequest.setCurrency(Currency.TRY.name());
        paymentRequest.setInstallment(1);
        paymentRequest.setBasketId("B" + request.getOrderId());
        paymentRequest.setPaymentChannel(PaymentChannel.WEB.name());
        paymentRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());

        PaymentCard card = new PaymentCard();
        card.setCardHolderName(request.getCardHolderName());
        card.setCardNumber(request.getCardNumber());
        card.setExpireMonth(request.getExpireMonth());
        card.setExpireYear(request.getExpireYear());
        card.setCvc(request.getCvc());
        card.setRegisterCard(0);
        paymentRequest.setPaymentCard(card);

        String holder = request.getCardHolderName() == null ? "Anonymous User" : request.getCardHolderName();
        String firstName;
        String lastName;
        int spaceIdx = holder.indexOf(' ');
        if (spaceIdx > 0) {
            firstName = holder.substring(0, spaceIdx);
            lastName = holder.substring(spaceIdx + 1);
        } else {
            firstName = holder;
            lastName = "User";
        }

        Buyer buyer = new Buyer();
        buyer.setId("BY-" + request.getUsername());
        buyer.setName(firstName);
        buyer.setSurname(lastName);
        buyer.setGsmNumber("+905350000000");
        buyer.setEmail(request.getUsername() + "@example.com");
        buyer.setIdentityNumber("11111111111");
        buyer.setRegistrationAddress("Test Address");
        buyer.setIp("127.0.0.1");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        paymentRequest.setBuyer(buyer);

        Address address = new Address();
        address.setContactName(holder);
        address.setCity("Istanbul");
        address.setCountry("Turkey");
        address.setAddress("Test Address");
        paymentRequest.setShippingAddress(address);
        paymentRequest.setBillingAddress(address);

        BasketItem item = new BasketItem();
        item.setId("BI-" + request.getOrderId());
        item.setName("Order " + request.getOrderId());
        item.setCategory1("General");
        item.setItemType(BasketItemType.PHYSICAL.name());
        item.setPrice(request.getAmount());
        List<BasketItem> items = new ArrayList<>();
        items.add(item);
        paymentRequest.setBasketItems(items);

        com.iyzipay.model.Payment payment = com.iyzipay.model.Payment.create(paymentRequest, options);

        IyzicoResult result = new IyzicoResult();
        result.setConversationId(conversationId);
        boolean success = "success".equalsIgnoreCase(payment.getStatus());
        result.setSuccess(success);
        if (success) {
            result.setPaymentId(payment.getPaymentId());
        } else {
            result.setErrorCode(payment.getErrorCode());
            result.setErrorMessage(payment.getErrorMessage() != null
                    ? payment.getErrorMessage() : "Payment declined");
            log.warn("Iyzico declined payment for conversationId {}: code={}, message={}",
                    conversationId, payment.getErrorCode(), payment.getErrorMessage());
        }
        return result;
    }
}
