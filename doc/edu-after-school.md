# 鍩轰簬 SpringBoot 涓?Vue 鐨勪腑灏忓鏅鸿兘璇惧悗鏈嶅姟骞冲彴

## 1. 椤圭洰瀹氫綅

鏈」鐩熀浜?`中小学智能课后服务平台` 浜屾寮€鍙戯紝鍥寸粫涓皬瀛﹁鍚庢湇鍔″钩鍙板満鏅疄鐜板鐢熴€佸闀裤€佹暀甯堛€佺鐞嗗憳鍥涚被瑙掕壊鐨勫崗鍚岀鐞嗭紝骞舵帴鍏ュぇ妯″瀷 API 瀹屾垚浣滀笟闂瓟銆侀€氱煡鐢熸垚鍜屾暀瀛﹀缓璁敓鎴愩€?
## 2. 鍔熻兘瀵瑰簲鍏崇郴

### 瀛︾敓

- 鏌ョ湅璇惧悗璇剧▼骞舵姤鍚?- 鎻愪氦浣滀笟闂骞舵煡鐪?AI 瑙ｇ瓟
- 鏌ョ湅涓汉璇剧▼鎶ュ悕鍜?AI 浜掑姩璁板綍

### 瀹堕暱

- 涓哄瀛愮淮鎶ゅ鐢熸。妗?- 涓哄瀛愭姤鍚嶈绋?- 鏌ョ湅瀛︿範璁板綍涓?AI 鍘嗗彶

### 鏁欏笀

- 鍙戝竷璇剧▼銆佺淮鎶よ绋嬩俊鎭?- 绠＄悊鎶ュ悕鍚嶅崟涓庡涔犺褰?- 浣跨敤 AI 鐢熸垚璇剧▼閫氱煡銆佹暀瀛﹀缓璁?
### 绠＄悊鍛?
- 缁熶竴绠＄悊璇剧▼銆佸鐢熸。妗堛€佹姤鍚嶈褰曘€佷綔涓氶棶绛斻€丄I 璋冪敤鏃ュ織
- 鏌ョ湅骞冲彴缁熻鐪嬫澘

## 3. 鍚庣鎺ュ彛妯″潡

- `/edu/dashboard` 骞冲彴缁熻鐪嬫澘
- `/edu/course` 璇剧▼绠＄悊涓庢姤鍚?- `/edu/student` 瀛︾敓妗ｆ涓庡闀跨粦瀹?- `/edu/enrollment` 鎶ュ悕璁板綍涓庡涔犺褰?- `/edu/question` 浣滀笟闂涓?AI 瑙ｇ瓟
- `/edu/aiLog` AI 璋冪敤鏃ュ織

## 4. 鏁版嵁搴撹剼鏈?
鎵ц鑴氭湰锛?
- `sql/ry_20260321.sql`
- `sql/edu_after_school.sql`
- `sql/edu_demo_data.sql`

鍏朵腑 `edu_after_school.sql` 鏂板浠ヤ笅涓氬姟琛細

- `edu_student_profile`
- `edu_course`
- `edu_course_enrollment`
- `edu_homework_question`
- `edu_ai_log`

鍚屾椂鍒濆鍖栦簡浠ヤ笅瑙掕壊锛?
- `edu_admin`
- `edu_teacher`
- `edu_parent`
- `edu_student`

`edu_demo_data.sql` 浼氳ˉ鍏咃細

- 4 涓紨绀鸿处鍙凤細`edu_admin / edu_teacher / edu_parent / edu_student`
- 3 闂ㄦ紨绀鸿绋?- 鎶ュ悕璁板綍銆佷綔涓氶棶绛斾笌 AI 璋冪敤鏃ュ織
- 鍙洿鎺ョ敤浜庨椤靛拰绛旇京婕旂ず鐨勬暟鎹?
## 5. AI 閰嶇疆

鍦?`eapple-admin/src/main/resources/application.yml` 涓柊澧炰簡锛?
```yml
edu:
  ai:
    enabled: false
    endpoint: https://api.openai.com/v1/chat/completions
    apiKey:
    model: gpt-4o-mini
```

璇存槑锛?
- 榛樿 `enabled: false`锛岀郴缁熶細璧版湰鍦?Mock 缁撴灉锛屾柟渚挎紨绀?- 鑻ュ～鍐欑湡瀹?API Key 骞跺紑鍚?`enabled: true`锛屽嵆鍙皟鐢ㄧ湡瀹炲ぇ妯″瀷
- 宸插鍔犳晱鎰熻瘝杩囨护涓庤緭鍏ラ暱搴﹂檺鍒讹紝渚夸簬淇濊瘉鍝嶅簲瀹夊叏鍙帶

## 6. 鍓嶇椤甸潰

鏂板椤甸潰濡備笅锛?
- `edu/dashboard/index` 鐪嬫澘
- `edu/course/index` 璇剧▼绠＄悊
- `edu/student/index` 瀛︾敓妗ｆ
- `edu/enrollment/index` 鎶ュ悕绠＄悊
- `edu/question/index` 浣滀笟闂瓟
- `edu/aiLog/index` AI 鏃ュ織

鐧诲綍鍚庣殑 `src/views/index.vue` 宸叉敼涓烘紨绀洪椤碉紝浼氳嚜鍔ㄥ睍绀猴細

- 骞冲彴鏍稿績缁熻
- 瑙掕壊婕旂ず娴佺▼
- 婕旂ず璐﹀彿璇存槑
- 蹇€熻繘鍏ヨ绋嬨€侀棶绛斻€丄I 鏃ュ織绛変笟鍔″叆鍙?
## 7. 褰撳墠娉ㄦ剰浜嬮」

- 褰撳墠鐜 Java 鐗堟湰涓?`1.8`锛岃€屽綋鍓嶈嫢渚濅富鍒嗘敮瑕佹眰 `JDK 17+`
- 鑻ヨ鏈湴缂栬瘧杩愯锛岃鍏堝垏鎹㈠埌 JDK 17 鎴栨洿楂樼増鏈?- 瀵煎叆 SQL 鍚庯紝闇€瑕佸湪绯荤粺鐢ㄦ埛涓墜鍔ㄧ粰涓嶅悓璐﹀彿鍒嗛厤 `edu_admin / edu_teacher / edu_parent / edu_student` 瑙掕壊

