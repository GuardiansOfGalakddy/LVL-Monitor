
# 한전 저압선로 감시 APP



&nbsp;&nbsp;한전 저압선로 장치의 이상이 생길 시 현장 작업자가 모든 장치를 직접 확인하지 않고, 전용 단말기를 통해 감시장치 및 수집기로부터 데이터를 전송받아 손쉽게 이상 장치 및 감지 센서를 파악할 수 있음.

&nbsp;&nbsp;이 프로젝트는 2019-2020 광운대학교 산학연계 SW프로젝트의 일환으로 진행되었으며, 문서의 하단에 있는 기여자(팀원, 지도교수)와 [비앤씨텍](http://www.bnctek.co.kr/)과 함께 진행되었습니다.


## 1. 설치방법




###### 사용 툴



* Android Studio(타겟 디바이스: Marshmallow이상)

* Tera Term

* Arduino



###### 설치방법

* 해당 Repository의 프로젝트를 Android Studio에서 open



###### 주의사항

* Google Map을 사용할 시 [**Google API Key**](https://cloud.google.com/maps-platform/)가 필요

*  **AndroidManifest** 파일에서 application 하위에 다음 코드를 입력

``` xml

<meta-data

android:name="com.google.android.maps.v2.API_KEY"

android:value="자신의 Google API Key"  />

```




## 2. 요구사항

* 해당 앱을 설치하려는 디바이스가 위치, 블루투스 기능을 지원

* 해당 앱을 사용하려면 위치, 블루투스 기능을 활성화

* 디바이스가 검색 가능한 모듈의 조건(ScanFilter)은 다음과 같다
   * RS = advertise하는 data에 manufacturer data가 설정되어 있을 것
      * Company code = 0x004C (Apple Inc)
      * Manufacturer data example = **0215**09DCCD7658C6878534EDA38DDBA1D47B**00010002C5**
         * **Bold = fixed data (Masked)**
         * 중간 16Byte는 UUID(AES-128bit-CBC 암호화)
            * 위의 경우 0F000101000000000000000000000000의 암호화
            * AES-Key와 IV는 코드에서 설정 가능

   * BS = advertise하는 data에 service UUID가 설정되어 있을 것
      * Service UUID = 0xFEFB (16Bit UUID, Telit Terminal I/O service)
         * 개발은 Telit사의 [**BlueMod+S42**](https://www.telit.com/m2m-iot-products/wifi-bluetooth-modules/bluemods42/) 모듈로 테스트하며 진행
         * 모듈은 Telit의 Terminal I/O service 지원 필요(History request/response 작업 관련)




## 3. 사용 방법



#### Main Activity
-----------------
Monitor 버튼: 모니터 액티비티로 이동

Collector 버튼: 수집기 액티비티로 이동

#### Navigation Drawer
-----------------
DBController 버튼: 저장된 item의 위도, 경도 DB 보기 및 삭제

오픈소스 라이센스: 사용된 오픈소스 라이센스 확인

#### MONITOR Activity
-----------------
스캔 시작
  * 버튼 클릭 시 5초간 RS 검색
  * 검색된 RS 모듈은 하단 Recycle view의 item으로 표시

구글 맵
  * 길게 클릭 시 마커 표시
  * 해당 마커 클릭 시 해당 위치의 위도, 경도, 모듈 이름을 설정하여 DB에 저장 가능

#### COLLECTOR Activity
-----------------

DISCOVER
  * 버튼 클릭 시 주변의 BS 모듈 검색, 검색된 BS 모듈은 하단 Recycle view의 item으로 표시

Recycle view
  * BS item 클릭 시 화면 전환
  * BS 모듈과 GATT connect 후 Telit Terminal I/O service 진입
  * 연결 완료 시 상단 Request History 버튼 활성화
  * 정상적으로 History 수신 시 하단 Recycle view에 item으로 추가
  * History item 클릭 시 상세 정보 Dialog로 확인 가능



## 4. 기여자

*  [**광운대학교**](https://www.kw.ac.kr/ko/)

+ 팀원

   + 박준화(팀장)

   + 천우진

   + 강승주

   + 최지훈

+ 지도교수

   + 최영근 교수
   + 엄영현 교수




## 5. 라이센스

*  [Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)


## 6. 문의 사항
* open Issues
* send email to :
   * JunHwaPark
      * wnsghk1025@naver.com
      * a28165415@gmail.com
      * <wnsghk1025@junhwapark.page>
   * Onegold11
      * ujini1129@gmail.com
   * superb1019
      * superb1019@naver.com
   * rkdtmdwn0923
      * rkdtmdwn0923@naver.com
