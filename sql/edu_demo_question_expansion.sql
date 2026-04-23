set names utf8mb4;

-- 作业问答扩展示例数据
-- 执行前请先完成：
-- 1. sql/qinghe_system_base.sql
-- 2. sql/edu_platform_upgrade.sql
-- 3. sql/edu_demo_course_expansion.sql
-- 4. sql/edu_demo_family_expansion.sql

delete from edu_homework_question where question_id between 200 and 229;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  200, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '循环里角色一直停不下来怎么办？',
  '我在编程课里让角色重复向前走 5 次，但是它会一直走不停。我不知道是循环次数没设对，还是少了停止条件。',
  '先检查重复次数有没有写成无限循环。如果题目只要求走 5 次，就选择“重复执行 5 次”，把移动动作放进去即可。做完后再单步运行，看看角色是不是刚好执行 5 次后停止。',
  '1', 'normal', '问答扩展数据', 'edu_student', date_sub(sysdate(), interval 6 day)
from edu_course_enrollment e where e.student_user_id = 113 and e.course_id = 1;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  201, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '调色后画面发灰怎么补救？',
  '我画花瓶的时候把蓝色和红色混了很多次，最后整张画都灰灰的，不知道要不要重新画。',
  '不用马上重画。可以先把画纸晾干，再用更干净的水重新铺一层主色。调色时尽量一次只混两种颜色，笔刷每次换色前都要洗净，这样颜色会更清透。',
  '1', 'normal', '问答扩展数据', 'edu_student', date_sub(sysdate(), interval 5 day)
from edu_course_enrollment e where e.student_user_id = 113 and e.course_id = 2;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  202, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '英文绘本里长句子总是读断怎么办？',
  '我读绘本的时候，看到长一点的句子就会中间停住，不知道应该在哪儿换气。',
  '可以先用斜线把句子分成两到三小段，再一小段一小段地读。先读顺，再连起来。碰到逗号或连接词时再轻轻停一下，会比一句话全憋着读更自然。',
  '1', 'normal', '问答扩展数据', 'edu_student_li_1', date_sub(sysdate(), interval 5 day)
from edu_course_enrollment e where e.student_user_id = 136 and e.course_id = 11;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  203, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '合唱时总是比别人快半拍怎么办？',
  '我唱到副歌的时候会越来越快，老师说我节奏有点抢，可是我自己唱的时候不太能发现。',
  '先别急着追求声音大，先跟着拍手节奏练。可以边数“1、2、3、4”边唱，每一拍只唱一个固定位置。多听伴奏里的鼓点，找到拍子后就不容易抢拍了。',
  '1', 'normal', '问答扩展数据', 'edu_parent_li', date_sub(sysdate(), interval 4 day)
from edu_course_enrollment e where e.student_user_id = 136 and e.course_id = 7;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  204, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '数学推理题不知道从哪里下手',
  '题目给了好几个条件，我一看就乱，不知道应该先圈数字，还是先找图形规律。',
  '先把题目条件分成“已知”和“要求”两部分。优先找最直接、最确定的信息，比如数量关系或明显规律，再把条件一条条代进去。不要一上来全算，先整理再动笔会更快。',
  '1', 'normal', '问答扩展数据', 'edu_student_li_2', date_sub(sysdate(), interval 4 day)
from edu_course_enrollment e where e.student_user_id = 137 and e.course_id = 4;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  205, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '传感器一会儿能用一会儿不能用',
  '我搭好的小车刚开始能感应，过一会儿就没有反应了，不知道是线松了还是程序错了。',
  '先按“硬件连接、供电、程序参数”三个顺序排查。先看导线有没有插紧，再检查电池和接口方向，最后再回到程序里看阈值设置。大多数这种问题都是连接不稳造成的。',
  '1', 'normal', '问答扩展数据', 'edu_parent_li', date_sub(sysdate(), interval 3 day)
from edu_course_enrollment e where e.student_user_id = 137 and e.course_id = 10;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  206, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '拼贴画排版总觉得太满',
  '我剪了很多小元素贴在纸上，最后感觉哪里都是东西，主题反而不明显了。',
  '可以先保留最大的主图，再删掉几块重复的小元素，让画面留出空白。拼贴画不是贴得越多越好，主题位置最显眼，其他元素围着它服务，整体会更舒服。',
  '1', 'normal', '问答扩展数据', 'edu_parent_zhang', date_sub(sysdate(), interval 3 day)
from edu_course_enrollment e where e.student_user_id = 138 and e.course_id = 2;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  207, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '钢琴左右手一合就乱',
  '我单手弹都还可以，但是左右手一起练的时候总会停下来，不知道该怎么分段。',
  '先把右手和左手各自弹熟，再只合两小节。合手时速度一定放慢，可以慢到原来的一半。只要节拍稳定，哪怕慢一点也没关系，熟了再提速。',
  '1', 'normal', '问答扩展数据', 'edu_student_zhang', date_sub(sysdate(), interval 2 day)
from edu_course_enrollment e where e.student_user_id = 138 and e.course_id = 8;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  208, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '带球时一抬头球就丢了',
  '我运球的时候如果看前面的人，脚下的球就容易跑掉，比赛里很紧张。',
  '先把带球分成两步练：低头控球和短时间抬头观察。可以每运三下球抬头看一次前方，慢慢把抬头时间加长。控球熟了以后，视线就能逐渐从球上移开。',
  '1', 'normal', '问答扩展数据', 'edu_student_chen', date_sub(sysdate(), interval 2 day)
from edu_course_enrollment e where e.student_user_id = 139 and e.course_id = 15;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  209, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '体能训练后小腿有点酸正常吗？',
  '昨天练完折返跑以后，小腿今天有点酸，但是还能走路，不知道要不要继续练。',
  '轻微酸胀一般是正常的训练反应。今天可以做拉伸和慢走，先不要做高强度冲刺。如果出现明显疼痛、肿胀或走路受影响，就要先休息并告诉老师或家长。',
  '1', 'normal', '问答扩展数据', 'edu_parent_chen', date_sub(sysdate(), interval 1 day)
from edu_course_enrollment e where e.student_user_id = 139 and e.course_id = 16;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  210, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '角色朗读时语气总是平',
  '老师让我扮演故事里的小兔子，可是我读出来像在背课文，没有故事感觉。',
  '先想清楚角色当时的心情，是开心、紧张还是着急。把重点词语稍微加重，再用停顿表现情绪变化。可以先听自己录音，再对照老师示范调整。',
  '1', 'normal', '问答扩展数据', 'edu_parent_wu', date_sub(sysdate(), interval 1 day)
from edu_course_enrollment e where e.student_user_id = 140 and e.course_id = 11;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  211, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '上台主持时忘词了怎么办',
  '我一紧张就会把准备好的开场白忘掉，站在前面脑子会空一下。',
  '可以把开场白拆成 3 个关键词记忆，不要整段死背。上台前先深呼吸，看一眼提示卡，再按照“问候、介绍、开始”三个步骤说，忘词时也能顺着关键词接下去。',
  '1', 'normal', '问答扩展数据', 'edu_student_wu_1', date_sub(sysdate(), interval 1 day)
from edu_course_enrollment e where e.student_user_id = 140 and e.course_id = 13;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  212, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '实验记录总是写不完整',
  '做完磁力实验以后，我只记得现象，不知道实验记录里是不是还要写步骤和结论。',
  '完整的实验记录通常写三部分：做了什么、看到了什么、说明了什么。也就是步骤、现象、结论。哪怕每一部分只写一两句话，也会比只写现象更完整。',
  '1', 'normal', '问答扩展数据', 'edu_student_wu_2', date_sub(sysdate(), interval 0 day)
from edu_course_enrollment e where e.student_user_id = 141 and e.course_id = 9;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  213, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '羽毛球发球老是不过网',
  '我练发球的时候不是太高就是直接下网，动作感觉很别扭。',
  '先把发球目标改成“稳定过网”而不是追求远。握拍放松一点，发球时用手腕轻送，不要猛甩。可以先站近一点练，等过网稳定后再慢慢拉远。',
  '1', 'normal', '问答扩展数据', 'edu_parent_wu', date_sub(sysdate(), interval 0 day)
from edu_course_enrollment e where e.student_user_id = 141 and e.course_id = 14;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  214, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '种植记录要写哪些内容？',
  '劳动课让我们记豆苗的变化，我不知道是不是每天都要写很多字。',
  '不用写很长。每次记录抓住 4 个点就够了：日期、天气、植物样子、自己的发现。比如“今天长高了 1 厘米，叶子更绿了”，这样就很完整。',
  '1', 'normal', '问答扩展数据', 'edu_student_sun', date_sub(sysdate(), interval 0 day)
from edu_course_enrollment e where e.student_user_id = 142 and e.course_id = 17;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  215, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '剪纸左右总是不对称',
  '我对折后剪出来的花纹一边大一边小，看起来不整齐。',
  '先在折好的纸上轻轻画出一半轮廓，再沿线慢慢剪。复杂图案不要一次剪到底，可以先剪大形，再修细节。对称作品最重要的是下刀前先想好结构。',
  '1', 'normal', '问答扩展数据', 'edu_parent_sun', date_sub(sysdate(), interval 0 day)
from edu_course_enrollment e where e.student_user_id = 142 and e.course_id = 18;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  216, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '口算一紧张就算错',
  '老师一计时我就会慌，本来会做的题也容易算错。',
  '先别把注意力放在“快”上，先保证前 5 题稳。可以把计时练习拆成短轮次，每次只做 1 分钟。正确率稳定以后，再一点点提速度，会比一上来追求很快更有效。',
  '1', 'normal', '问答扩展数据', 'edu_parent_zhao', date_sub(sysdate(), interval 0 day)
from edu_course_enrollment e where e.student_user_id = 143 and e.course_id = 6;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  217, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '演讲时手不知道放哪里',
  '我上台说话的时候手总是乱动，越想控制越紧张。',
  '可以先给自己设计两个固定动作，比如开头双手自然放前面，强调重点时再抬一只手。动作不用多，稳定就好。提前对着镜子练几遍，会更安心。',
  '1', 'normal', '问答扩展数据', 'edu_student_zhao_1', date_sub(sysdate(), interval 0 day)
from edu_course_enrollment e where e.student_user_id = 143 and e.course_id = 12;

insert into edu_homework_question (
  question_id, course_id, course_name, student_user_id, student_name, parent_user_id, teacher_user_id,
  question_title, question_content, ai_answer, answer_status, safety_flag, remark, create_by, create_time
)
select
  218, e.course_id, e.course_name, e.student_user_id, e.student_name, e.parent_user_id, e.teacher_user_id,
  '非遗手作步骤一多就记混了',
  '老师示范的时候我能看懂，但轮到自己做就容易把顺序搞反。',
  '可以把步骤先记成 1、2、3、4 四个短词，比如“裁、折、贴、修”。每做一步就对照一次，不要全凭记忆往后冲。手作类任务最怕顺序乱，慢一点反而更稳。',
  '1', 'normal', '问答扩展数据', 'edu_parent_zhao', date_sub(sysdate(), interval 0 day)
from edu_course_enrollment e where e.student_user_id = 144 and e.course_id = 18;

