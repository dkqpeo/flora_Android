package com.example.flora

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flora.databinding.TossTestBinding
import com.tosspayments.paymentsdk.PaymentWidget
import com.tosspayments.paymentsdk.model.PaymentCallback
import com.tosspayments.paymentsdk.model.PaymentWidgetStatusListener
import com.tosspayments.paymentsdk.model.TossPaymentResult
import com.tosspayments.paymentsdk.view.PaymentMethod
import retrofit2.Call
import retrofit2.Response
import java.io.Serializable

class TossTest : AppCompatActivity(), Serializable {
    private lateinit var binding: TossTestBinding
    var orderInfo = arrayListOf<TossModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TossTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderInfo = intent.getSerializableExtra("orderInfo") as ArrayList<TossModel>
        Log.d("orderSize", orderInfo.size.toString())

        val paymentWidget = PaymentWidget(
            activity = this@TossTest,
            clientKey = "test_ck_kYG57Eba3Gbq1MEAen9Q8pWDOxmA",
            customerKey = "PaymentWidget.ANONYMOUS",
        )

        val paymentMethodWidgetStatusListener = object : PaymentWidgetStatusListener{
            override fun onLoad() {
                val message = "결제위젯 렌더링 완료"
                Log.d("PaymentWidgetStatusListener", message)
            }
        }
        /*orderInfo.get(1).price*/
        paymentWidget.run {
            renderPaymentMethods(
                method = binding.paymentMethodWidget,
                amount = PaymentMethod.Rendering.Amount(10),
                paymentWidgetStatusListener = paymentMethodWidgetStatusListener
            )

            renderAgreement(binding.agreementWidget)
        }

        binding.requestPaymentCta.setOnClickListener {
            var orderId = orderInfo.get(1).orderId
            var orderName = orderInfo.get(1).flowerName

            paymentWidget.requestPayment(
                paymentInfo = PaymentMethod.PaymentInfo(orderId , orderName),
                paymentCallback = object : PaymentCallback {
                    override fun onPaymentSuccess(success: TossPaymentResult.Success) {
                        Log.i("toss", success.paymentKey)
                        Log.i("toss", success.orderId)
                        Log.i("toss", success.amount.toString())

                        var reqPayment = PaymentModel(success.paymentKey, success.orderId, success.amount.toInt())
                        RetrofitObject.getTossService().reqPayment(reqPayment)
                            .enqueue(object : retrofit2.Callback<TossResp>{
                                override fun onResponse(
                                    call: Call<TossResp>,
                                    response: Response<TossResp>
                                ) {
                                    if(response.body()?.mId != null){
                                        Toast.makeText(
                                            baseContext,
                                            "주문을 성공 하였습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<TossResp>, t: Throwable) {
                                    Log.e("fail", t.message.toString())
                                }
                            })
                    }

                    override fun onPaymentFailed(fail: TossPaymentResult.Fail) {
                        Log.e("tossFail",fail.errorMessage)
                    }
                }
            )
        }

    }
}