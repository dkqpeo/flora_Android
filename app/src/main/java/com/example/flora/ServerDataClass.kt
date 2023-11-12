package com.example.flora

import java.time.LocalDate
import java.util.Date

// 서버로 전송할 데이터 클래스

// 로그인 DTO
data class LoginModel(
    var userId : String ,
    var password : String
)

// 회원가입 DTO
data class JoinModel(
    var userId : String ,
    var password : String ,
    var userName: String,
    val tel: String,
    val email: String
)

// 회원 정보수정 DTO
data class UpdateUserModel(
    var seq: Long,
    var userId: String,
    var userName: String,
    var oldPassword : String ,
    var password : String ,
    val tel: String,
    val email: String
)

// 장바구니 추가 DTO
data class CartModel(
    val flowerName: String,
    val price: Int,
    val flowerImg: String,
    val userName: String,
)

// 토스 결제 페이지로 넘길 DTO
data class TossModel(
    var orderId: String,
    val flowerName: String,
    val price: Int,
    val flowerImg: String,
    val userName: String,
    val postNum: String,
    val address: String,
    val detailAddress: String,
    val fromUser: String,
    val fromUserTel: String
)

// 토스 결제요청 DTO
data class PaymentModel(
    var paymentKey: String,
    var orderId: String,
    var amount: Int
)

// 토스 결재 응답 DTO
data class TossResp(
    var mId: String,
    var lastTransactionKey: String,
    var status: String,

)

// 상품 주문 DTO
data class OrderModel(
    var orderId: String,
    val flowerName: String,
    val price: Int,
    val flowerImg: String,
    val userName: String,
    val postNum: String,
    val address: String,
    val detailAddress: String,
    val fromUser: String,
    val fromUserTel: String,
    val paymentKey: String
)

// 서버 응답을 받을 데이터클래스
data class UserResp(
    val status: String ,
    val message: String ,
    val data: UserData
)

data class UserData(
    val seq: Long,
    val userId: String,
    val userName: String,
    val tel: String,
    val email: String
)

// 서버 응답을 받을 데이터클래스
data class OrderResp(
    val status: String ,
    val message: String ,
    val data: OrderData
)

// 주문 목록
data class OrderListResp(
    val status: String ,
    val message: String ,
    val data: MutableList<OrderData>
)

data class OrderData(
    val seq: Long,
    val date: Date,
    val flowerName: String,
    val price: Int,
    val flowerImg: String,
    val postNum: String,
    val address: String,
    val detailAddress: String,
    val fromUser: String,
    val fromUserTel: String,
    val status: Int,
    val userSeq: UserData
)

// 서버 응답을 받을 데이터클래스
data class CartAddResp(
    val status: String ,
    val message: String ,
    val data: CartData
)

// 서버 응답을 받을 데이터클래스
data class CartResp(
    val status: String ,
    val message: String ,
    val data: MutableList<CartData>
)

data class CartData(
    val seq: Long,
    val flowerName: String,
    val price: Int,
    val flowerImg: String,
    val userSeq: UserData
)

// 서버 응답을 받을 데이터클래스
data class CartDelResp(
    val status: String ,
    val message: String ,
    val data: Boolean
)