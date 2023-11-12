package com.example.flora


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // 꽃 상세 조회
    @GET("1390804/NihhsTodayFlowerInfo01/selectTodayFlowerView01?serviceKey=" + BuildConfig.API_KEY)
    fun getFlower(@Query("dataNo") dataNo: Int): Call<Flower>

    // 오늘의 꽃 조회
    @GET("1390804/NihhsTodayFlowerInfo01/selectTodayFlower01?serviceKey=" + BuildConfig.API_KEY)
    fun getTodayFlower(): Call<Flower>

    // 오늘의 곷 목록 조회
    @GET("1390804/NihhsTodayFlowerInfo01/selectTodayFlowerList01?serviceKey=" + BuildConfig.API_KEY)
    fun getFlowerList(@Query("pageNo") pageNo: Int): Call<Flower>

    // 꽃 이름으로 검색
    @GET("1390804/NihhsTodayFlowerInfo01/selectTodayFlowerList01?serviceKey=" + BuildConfig.API_KEY + "&searchType=1")
    fun getFlowerSearchName(@Query("searchWord")searchWord : String): Call<Flower>

    // 꽃말로 검색
    @GET("1390804/NihhsTodayFlowerInfo01/selectTodayFlowerList01?serviceKey=" + BuildConfig.API_KEY + "&searchType=4")
    fun getFlowerSearchLang(@Query("searchWord")searchWord : String): Call<Flower>

    // 날짜로 꽃 검색
    @GET("1390804/NihhsTodayFlowerInfo01/selectTodayFlowerList01?serviceKey=" + BuildConfig.API_KEY )
    fun getFlowerSearchDate(@Query("fMonth")month : String, @Query("fDay")day : String ): Call<Flower>


    // 백엔드 서버 관련 API
    // 로그인 요청
    @POST("/api/users/login")
    fun userLogin(@Body loginModel: LoginModel): Call<UserResp>

    // 회원가입 요청
    @POST("/api/users/signUp")
    fun userSignUp(@Body joinModel: JoinModel): Call<UserResp>

    @PUT("/api/users/update")
    fun updateUser(@Body updateUserModel: UpdateUserModel): Call<UserResp>

    // 상품 주문 요청
    @POST("api/order/new")
    fun createOrder(@Body orderModel: OrderModel): Call<OrderResp>

    // 장바구니에 상품 추가 요청
    @POST("api/cart/new")
    fun addCart(@Body cartModel: CartModel): Call<CartAddResp>

    // 장바구니 아이템 삭제
    @DELETE("api/cart/del/{seq}")
    fun deleteItem(@Path(value = "seq") seq : Long): Call<CartDelResp>

    // 장바구니 목록 리스트
    @GET("api/cart/{name}")
    fun cartList(@Path(value = "name")name : String): Call<CartResp>

    // 주문 목록 리스트
    @GET("api/order/{name}")
    fun orderList(@Path(value = "name")name : String): Call<OrderListResp>

    // Toss API
    // 결제 요청
    @Headers("Authorization: Basic dGVzdF9za195TDBxWjRHMVZPUE4xMFJtUldrbnJvV2IyTVFZOg==")
    @POST("/v1/payments/confirm")
    fun reqPayment(@Body paymentModel: PaymentModel): Call<TossResp>
}