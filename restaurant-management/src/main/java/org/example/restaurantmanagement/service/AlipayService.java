package org.example.restaurantmanagement.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurantmanagement.config.AlipayConfig;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 支付宝（沙箱）支付服务
 * <p>
 * 仅演示用：所有凭据硬编码在 AlipayConfig 中，生产环境务必改为配置注入。
 * </p>
 */
@Slf4j
@Service
public class AlipayService {

    /**
     * 构造 AlipayClient
     */
    private AlipayClient buildClient() {
        return new DefaultAlipayClient(
                AlipayConfig.GATEWAY_URL,
                AlipayConfig.APP_ID,
                AlipayConfig.APP_PRIVATE_KEY,
                AlipayConfig.FORMAT,
                AlipayConfig.CHARSET,
                AlipayConfig.ALIPAY_PUBLIC_KEY,
                AlipayConfig.SIGN_TYPE);
    }

    /**
     * 创建电脑网站支付（页面跳转）表单 HTML。
     * <p>
     * 返回一段会自动提交的 HTML &lt;form&gt;，浏览器渲染时将跳转到支付宝收银台。
     * </p>
     *
     * @param orderId    订单号（雪花 ID，字符串形式传递避免精度丢失）
     * @param totalAmount 订单金额（单位：元，两位小数）
     * @param subject     订单标题
     * @return 可直接渲染的 HTML 表单（含自动 submit 的 JS）
     */
    public String createPagePayForm(
            String orderId,
            BigDecimal totalAmount,
            String subject) throws AlipayApiException {

        AlipayClient alipayClient = buildClient();

        // 业务参数
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(orderId);
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setTotalAmount(totalAmount.toPlainString());
        model.setSubject(subject);

        // 构造请求
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(AlipayConfig.NOTIFY_URL);
        request.setReturnUrl(AlipayConfig.RETURN_URL);
        request.setBizModel(model);

        // pageExecute 返回的 body 即为 HTML 表单（含 _input_charset、sign、biz_content 等隐藏域）
        String form = alipayClient.pageExecute(request).getBody();

        log.info(
                "[Alipay] 创建支付订单 out_trade_no={}, amount={}, subject={}",
                orderId, totalAmount, subject);

        return form;
    }

    /**
     * 主动查询订单支付状态。
     *
     * @param outTradeNo 商户订单号（即雪花 ID 的字符串形式）
     * @return true 表示已支付成功（TRADE_SUCCESS / TRADE_FINISHED）
     */
    public boolean isPaid(String outTradeNo) throws AlipayApiException {
        AlipayClient alipayClient = buildClient();

        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(outTradeNo);

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizModel(model);

        AlipayTradeQueryResponse response = alipayClient.execute(request);

        if (!response.isSuccess()) {
            log.warn(
                    "[Alipay] 查询失败 out_trade_no={}, subMsg={}",
                    outTradeNo, response.getSubMsg());
            return false;
        }

        String tradeStatus = response.getTradeStatus();
        boolean paid = "TRADE_SUCCESS".equals(tradeStatus)
                || "TRADE_FINISHED".equals(tradeStatus);

        log.info(
                "[Alipay] 查询 out_trade_no={}, tradeStatus={}, paid={}",
                outTradeNo, tradeStatus, paid);

        return paid;
    }
}