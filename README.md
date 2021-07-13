# project_parkingAPP 智慧停車場預約系統APP #
## :heart:系統介紹 ##
<br /><p>With the advancement of technology, mobile devices and mobile networks are popularized in the society, and the information available makes life more convenient.
<br /><p>The public can make reservations in advance to know the location of the parking lot and get the nearby parking lot information from the application. To integrate the system by Google Cloud Firebase, when the vehicle reservation is completed, the license plate recognition system can control the problem of unable to enter the parking lot from the reservation,
reducingthe parking trouble in popular attractions and suburbs.
<br /><p>Our system uses the mobile phone to reserve a parking space online, upload the basic information and license plate number filled in by each owner to the cloud database in the application, and you can enter the parking lot directly after the reservation is completed. Before picking up the car, the owner only needs to check the "Payment Settings" in the application to see if the wallet is sufficient to pay for the parking fee, without waiting in line to leave the car,
and enjoy the convenience brought by this system to everyone.
## :dart:系統架構圖 ##

<img src="samh物聯網系統圖_Socket.png"/>

## :dart:Navigation Drawer 側選單 列表 ##

<img width="250" height="500" src="navigation.jpg"/>

<br />:dart:1.側選單上方(簡易個人資料)
----------------------------
<br /><p>(1)大頭貼: 由使用者個人資料上傳於Firestorage，抓取下來
<br />  (2)姓名、Email: 使用者個人資料編輯的姓名及註冊email
<br />  (3)登入/登出: 使用者登入或登出

<br />:dart:2.個人檔案:
---------------------------
<br /><p>使用者登入後，即可進行查看個人資料以及編輯個人資料(大頭貼、姓名、連絡電話、車牌)，
我們使用Firebase提供的後端服務包含即時資料庫（Realtime database）、簡單的身分驗證模組（Authentication）、檔案儲存（Storage）。
<br /><p><img width="250" height="500" src="profile.jpg"/>
<img width="250" height="500" src="profile_edit.jpg"/>
<img width="250" height="500" src="upload_profile_pic.jpg"/>

<br /><p><img width="1500" height="500" src="user_info.PNG"/>


<br />:dart:3.尋找停車場:
---------------------------
<br /><p>(1)Google Map地圖:
<br /><p>在Google Map上，隨著手機螢幕滑動，讀取螢幕最佳中心位置附近的停車API(座標、總車位、剩餘車位、費率、是否營業中、今日營業時間、地址、電話...等等)，
顯示標記(Marker)於地圖上(特約停車場icon與一般停車場icon不同)。使用者也可以透過語音或文字搜尋地點，方便找尋停車場，也可以導航至停車場。

<br /><p><img width="250" height="500" src="home.jpg"/>

<br /><p>(2)點擊marker icon的infowindow，顯示停車場詳細資訊:
<br /><p>顯示停車場詳細資訊，下方按鈕方別有預約按鈕、街景按鈕、最愛按鈕、返回鍵按鈕。
<br /><p><img width="250" height="550" src="parkinglot_infomation.jpg"/>

<br /><p>(3)點擊預約按鈕，進入預約介面:
<br /><p>Step1 個人資料填寫: (從firebase使用者個人檔案抓取資料下來)，並且選擇欲預約的時間
<br /><p>Step2 付款資訊:
<br /><p>資料填寫完畢後，按下預約按鈕，即將上方詳細資料上傳至Firebase(停車場管理、個人預約紀錄)
<br /><p><img width="250" height="600" src="reservate.jpg"/>
<br /><p><img width="1500" height="500" src="user_info_record.PNG"/>
<br /><p><img width="1500" height="500" src="reservatable_parkinglot_data.PNG"/>

<br />:dart:4.預約紀錄:
---------------------------
<br /><p>抓取FireBase資料下來，以ListView Adapter形式顯示，顯示歷史預約紀錄，若要結束預約付費，可從預約紀錄裡結束預約並付費。
<br /><p><img width="250" height="500" src="record.jpg"/>

<br />:dart:5.我的最愛:
---------------------------
<br /><p>我的最愛功能，使用SQL本地儲存資料，以ListView Adapter形式顯示，點擊即可立即畫面移動於GoogleMap地圖上。
<br /><p><img width="250" height="500" src="myfavorite.jpg"/>

<br />:dart:6.智慧停車場預約系統APP 系統執行流程圖:
---------------------------
<br /><p>專題執⾏流程圖:
<br /><p><img width="250" height="700" src="pi流程.PNG"/>
  <br /><p>⼿機應用程式之主頁面流程圖:
<br /><p><img width="1000" height="1000" src="⼿機應用程式之主頁面流程圖.PNG"/>
  <br /><p>⼿機應用程式之側邊選單-停車相關資訊:
<br /><p><img width="1000" height="1000" src="⼿機應用程式之側邊選單-停車相關資訊.PNG"/>
  <br /><p>⼿機應用程式之側邊選單-使用者資訊:
<br /><p><img width="1000" height="1000" src="⼿機應用程式之側邊選單-使用者資訊.PNG"/>
  <br /><p>⼿機應用程式之側邊選單-回饋及製作團隊:
<br /><p><img width="1000" height="800" src="⼿機應用程式之側邊選單-回饋及製作團隊.PNG"/>

