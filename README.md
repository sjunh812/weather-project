# ⛅ Weather Test App - 인앱 업데이트 테스트 앱
![ic_launcher](https://user-images.githubusercontent.com/79048895/168544444-d30c11ee-bd44-40ac-9eb9-5c75c0569228.png)
<br>
<br>
## 📷 스크린샷
<img src="https://user-images.githubusercontent.com/79048895/168541864-53e3dbf9-1713-441c-9d3a-6cbce32c70ed.jpg" width="180" height="390" />  <img src="https://user-images.githubusercontent.com/79048895/168541873-e15db9a2-7fc9-43d2-aced-0abfcd08c14a.jpg" width="180" height="390" />  <img src="https://user-images.githubusercontent.com/79048895/168541881-5dac570a-e3bc-44d8-8a71-1361da9c0c6d.jpg" width="180" height="390" />  <img src="https://user-images.githubusercontent.com/79048895/168541886-1b68bd7c-a2a2-492f-99bc-a1583f86580b.jpg" width="180" height="390" />
<br>
<br>  
## 📝 프로젝트 소개
### 인앱 업데이트 테스트 겸 만든 간단한 날씨 정보 앱입니다.

안드로이드에서 제공하는 인앱 업데이트 기능을 직접 테스트  
해보기 위해 제작한 간단한 날씨 정보 앱입니다.  
현재 위치하고 있는 지역의 간단한 날씨 정보를 한눈에 알아볼 수 있고,   
지역 검색을 통해 특정 지역의 날씨 정보도 확인할 수 있습니다.  
인앱 업데이트의 경우 업데이트가 있을때 강제로 업데이트 되도록 하였습니다.  

사용된 기술스택은 아래와 같습니다.
- Kotlin
- ViewModel / LiveData
- Coroutine
- Naver map
- Google play core(인앱 업데이트)
- Retrofit2
- Hilt
<br>

## ✍ 느낀점
진행 중인 프로젝트에서 인앱 업데이트 테스트가 필요하여 직접  
구글 플레이스토어 올린 앱입니다. 그래도 마켓에 직접 프로모션 출시하기 때문에  
인앱 업데이트 기능 외에도 간단한 날씨를 확인할 수 있는 기능도 추가하였습니다.  
인앱 업데이트 기능의 경우 정상적으로 동작하는 것을 확인했고,    
서버를 이용한다면 업데이트의 중요도에 따라 업데이트 모드를 *IMMEDIATE(강제)*로 할지,  
*FLEXIBLE(선택)*로 할지 선택하여 구현할 수 있다는 점이 매력적이었습니다.    
또 *MVVM* 패턴과 *Hilt*, *Coroutine*과 같은 기술 스택을 이용하므로서    
안드로이드 최신 기술을 실습하는 기회를 가질 수 있는 프로젝트였습니다!
