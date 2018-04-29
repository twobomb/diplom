package com.twobomb.app;

import com.twobomb.entity.*;
import com.twobomb.entity.TeacherInfo;
import com.twobomb.repository.*;
import com.twobomb.service.DisciplineService;
import com.twobomb.service.PersonService;
import com.twobomb.service.ThemeService;
import com.twobomb.service.UtilService;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.hibernate.*;
import org.hibernate.validator.internal.IgnoreForbiddenApisErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;


@SpringComponent
public class DataGeneration {

    UserRepository userRepository;
    GroupRepository groupRepository;
    RoleRepository roleRepository;
    PersonRepository personRepository;
    DiscinplineRepository discinplineRepository;
    CourseworkRepository courseworkRepository;
    PasswordEncoder passwordEncoder;
    DisciplineService disciplineService;
    PersonService personService;
    TeacherInfoRepository teacherInfoRepository;
    ControlDisciplineRepository controlDisciplineRepository;
    ThemeRepository themeRepository;
    ThemeService themeService;
    Random rnd;

    public DataGeneration(UserRepository userRepository,GroupRepository groupRepository,RoleRepository roleRepository,PersonRepository personRepository,PasswordEncoder passwordEncoder,DiscinplineRepository discinplineRepository,CourseworkRepository courseworkRepository,DisciplineService disciplineService,PersonService personService,TeacherInfoRepository teacherInfoRepository,ControlDisciplineRepository controlDisciplineRepository,ThemeService themeService,ThemeRepository themeRepository) {
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.personRepository = personRepository;
    this.discinplineRepository = discinplineRepository;
    this.courseworkRepository = courseworkRepository;
    this.disciplineService = disciplineService;
    this.personService = personService;
    this.teacherInfoRepository = teacherInfoRepository;
    this.controlDisciplineRepository = controlDisciplineRepository;
    this.themeRepository = themeRepository;
    this.themeService = themeService;
    rnd  = new Random();
    }

    @Autowired
    LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;


    @Autowired
    Environment environment;
    @PostConstruct
    public void create(){
        //Если типа update То генератор не сработает
        if(environment.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto").equals("update"))
            return;
        createRoles();
        createUsers();
        createGroups();

        createPerson();

        createDisciplines();
        createCourseworks();

        //Привязка преподователей к дисциплинам
        bindTeacherToDisciplines();
        //Привязка дисциплин к группам
        bindGroupsToDisciplines();
        //Назначение кол-ва тем которые преподаватель должен подать к каждой привязанной к нему курсовой
        createTeacherInfoCoursework();

        //Генерация рандомным дисциплинам(не всем) рандомных параметров
        createDisciplineControl();

        //Генерирует темы
        createThemes();

        //Привязывает студентов к темам
        bindStudentsToTheme();

    }

    private void bindStudentsToTheme() {
        SessionFactory sessionFactory =  localContainerEntityManagerFactoryBean.getObject().unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        try {
            List<Discipline> disciplineList = discinplineRepository.findAll();
            List<CourseWork> courseworksList = new ArrayList<>();
            disciplineList.forEach( discipline -> courseworksList.addAll(discipline.getCourseWork()));

            for(CourseWork cw:courseworksList){
                cw = session.get(cw.getClass(),cw.getId());
                List<Group> groupList = cw.getDiscipline().getGroups();
                for(Group group:groupList){
                    List<Theme> freeThemes = disciplineService.getFreeThemeListFromCoursework(cw,group);
                    for(Person student:group.getPersons()){
                        //50% что студент будет привязан к теме
                        if(freeThemes.size()>0 && rnd.nextBoolean() ){
                            Theme rndTheme = freeThemes.get(rnd.nextInt(freeThemes.size()));
                            freeThemes.remove(rndTheme);
                            rndTheme.addAttachStudentAndCoursework(student,cw);
                            themeRepository.save(rndTheme);
                        }
                    }
                }
            }
            themeRepository.flush();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            session.close();
        }
    }

    public void createPerson(){
        String[] name = new String[]{"Абрам","Аваз","Аввакум","Август","Августин","Авдей","Авраам","Автандил","Агап","Агафон","Аггей","Адам","Адис","Адольф","Адриан","Азамат","Айдар","Айнур","Айрат","Аким","Алан","Алей","Александр","Алексей","Али","Альберт","Альфред","Амадей","Амадеус","Амаяк","Амвросий","Ананий","Анастасий","Анатолий","Анвар","Ангел","Андоим","Андрей","Аникита","Антон","Арам","Арий","Аристарх","Аркадий","Арман","Арно","Арнольд","Арон","Арсен","Арсений","Арслан","Артем","Артемий","Артур","Архип","Аскар","Аскольд","Аслан","Афанасий","Ахмет","Ашот","Бальтазар","Бежен","Бенедикт","Берек","Бернард","Бертран","Богдан","Болеслав","Борис","Бронислав","Булат","Вадим","Валентин","Валерий","Вальтер","Варфоломей","Василий","Вацлав","Велизар","Венедикт","Вениамин","Викентий","Виктор","Вилли","Вильгельм","Виссарион","Виталий","Витольд","Владимир","Владислав","Владлен","Володар","Вольдемар","Всеволод","Вячеслав","Гавриил","Галактион","Гарри","Гастон","Гаяс","Гевор","Геннадий","Генрих","Георгий","Геракл","Геральд","Герасим","Герман","Глеб","Гордей","Гордон","Горислав","Градимир","Григорий","Гурий","Густав","Давид","Дамир","Даниил","Даниэль","Данияр","Дарий","Дементий","Демид","Демосфен","Демьян","Денис","Джамал","Джордан","Дмитрий","Добрыня","Дональд","Донат","Дорофей","Евгений","Евграф","Евдоким","Евсевий","Евсей","Евстафий","Егор","Елеазар","Елисей","Емельян","Еремей","Ермолай","Ерофей","Ефим","Ефрем","Жан","Ждан","Жорж","Захар","Зиновий","Ибрагим","Иван","Игнатий","Игорь","Илларион","Ильдар","Ильнар","Ильнур","Илья","Ильяс","Иннокентий","Иоанн","Иосиф","Ипполит","Искандер","Ислам","Камиль","Карим","Карл","Кирилл","Клим","Кондрат","Константин","Корней","Кузьма","Лавр","Лаврентий","Лев","Леон","Леонид","Леонтий","Леопольд","Лука","Лукьян","Любим","Макарий","Максим","Максимилиан","Марат","Марк","Марсель","Мартин","Матвей","Мирон","Мирослав","Митрофаний","Михаил","Михей","Мишель","Мстислав","Мурат","Муслим","Назар","Назарий","Наиль","Натан","Наум","Нестор","Никанор","Никита","Никифор","Николай","Никон","Нифонт","Норман","Овидий","Олан","Олег","Олесь","Онисим","Орест","Осип","Оскар","Павел","Петр","Платон","Прохор","Равиль","Радик","Радмир","Радомир","Раду","Рамазан","Рамиль","Ратибор","Ратмир","Рафаэль","Рахим","Рашид","Ринат","Ричард","Роберт","Родион","Ролан","Роман","Ростислав","Рудольф","Руслан","Рустам","Сабир","Савва","Савелий","Салим","Самвел","Самсон","Святослав","Севастьян","Семён","Серафим","Сергей","Соломон","Спартак","Спиридон","Станислав","Степан","Стефан","Сулейман","Султан","Тагир","Тамерлан","Тарас","Теодор","Тигран","Тимофей","Тимур","Тихон","Трофим","Умар","Устин","Фаддей","Фарид","Федор","Федот","Феликс","Феодосий","Фердинанд","Фидель","Филимон","Филипп","Флор","Флорентий","Фома","Франц","Фридрих","Фуад","Харитон","Хасан","Христиан","Христос","Христофор","Шамиль","Эдвард","Эдгар","Эдуард","Эльдар","Эмиль","Эмин","Эмир","Эммануил","Эрик","Эрнест","Юлиан","Юлий","Юрий","Юсуф","Юхим","Яким","Яков","Ян","Януарий","Яромир","Ярослав","Ясон"};
        String[] secondname = new String[]{"Абрамов","Авдеев","Агафонов","Аксёнов","Александров","Алексеев","Андреев","Анисимов","Антонов","Артемьев","Архипов","Афанасьев","Баранов","Белов","Белозёров","Белоусов","Беляев","Беляков","Беспалов","Бирюков","Блинов","Блохин","Бобров","Бобылёв","Богданов","Большаков","Борисов","Брагин","Буров","Быков","Васильев","Веселов","Виноградов","Вишняков","Владимиров","Власов","Волков","Воробьёв","Воронов","Воронцов","Гаврилов","Галкин","Герасимов","Голубев","Горбачёв","Горбунов","Гордеев","Горшков","Григорьев","Гришин","Громов","Гуляев","Гурьев","Гусев","Гущин","Давыдов","Данилов","Дементьев","Денисов","Дмитриев","Доронин","Дорофеев","Дроздов","Дьячков","Евдокимов","Евсеев","Егоров","Елисеев","Емельянов","Ермаков","Ершов","Ефимов","Ефремов","Жданов","Жуков","Журавлёв","Зайцев","Захаров","Зимин","Зиновьев","Зуев","Зыков","Иванков","Иванов","Игнатов","Игнатьев","Ильин","Исаев","Исаков","Кабанов","Казаков","Калашников","Калинин","Капустин","Карпов","Кириллов","Киселёв","Князев","Ковалёв","Козлов","Колесников","Колобов","Комаров","Комиссаров","Кондратьев","Коновалов","Кононов","Константинов","Копылов","Корнилов","Королёв","Костин","Котов","Кошелев","Красильников","Крылов","Крюков","Кудрявцев","Кудряшов","Кузнецов","Кузьмин","Кулагин","Кулаков","Куликов","Лаврентьев","Лазарев","Лапин","Ларионов","Лебедев","Лихачёв","Лобанов","Логинов","Лукин","Лыткин","Макаров","Максимов","Мамонтов","Марков","Мартынов","Маслов","Матвеев","Медведев","Мельников","Меркушев","Миронов","Михайлов","Михеев","Мишин","Моисеев","Молчанов","Морозов","Муравьёв","Мухин","Мышкин","Мясников","Назаров","Наумов","Некрасов","Нестеров","Никитин","Никифоров","Николаев","Никонов","Новиков","Носков","Носов","Овчинников","Одинцов","Орехов","Орлов","Осипов","Павлов","Панов","Панфилов","Пахомов","Пестов","Петров","Петухов","Поляков","Пономарёв","Попов","Потапов","Прохоров","Рогов","Родионов","Рожков","Романов","Русаков","Рыбаков","Рябов","Савельев","Савин","Сазонов","Самойлов","Самсонов","Сафонов","Селезнёв","Селиверстов","Семёнов","Сергеев","Сидоров","Силин","Симонов","Ситников","Соболев","Соколов","Соловьёв","Сорокин","Степанов","Стрелков","Субботин","Суворов","Суханов","Сысоев","Тарасов","Терентьев","Тетерин","Тимофеев","Титов","Тихонов","Третьяков","Трофимов","Туров","Уваров","Устинов","Фадеев","Фёдоров","Федосеев","Федотов","Филатов","Филиппов","Фокин","Фомин","Фомичёв","Фролов","Харитонов","Хохлов","Цветков","Чернов","Шарапов","Шаров","Шашков","Шестаков","Шилов","Ширяев","Шубин","Щербаков","Щукин","Юдин","Яковлев","Якушев"};
        List<User> users = userRepository.findAll();
        List<Group> groups = groupRepository.findAll();
        for(User u:users) {
            Group g = u.getRole().getRole().equals(Role.STUDENT)? groups.get(rnd.nextInt(groups.size())):null;
            personRepository.save(new Person(name[rnd.nextInt(name.length)], secondname[rnd.nextInt(secondname.length)], u, g));
        }
        personRepository.flush();
    }
    public void createRoles(){
        roleRepository.save(new Role("Администратор",Role.ADMIN));
        roleRepository.save(new Role("Преподаватель",Role.TEACHER));
        roleRepository.save(new Role("Студент",Role.STUDENT));
        roleRepository.flush();
    }
//Создание и добавление тем
    public void createThemes(){
        SessionFactory sessionFactory =  localContainerEntityManagerFactoryBean.getObject().unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        try {
            String[] themes = new String[]{"Основные тенденции изменений в области экономической активности и занятости населения", "Население и общественное здоровье как составляющая часть человеческого потенциалаРоссийского общества", "Влияние миграционных процессов на человеческий потенциал Российской Федерации", "Образование и развитие человеческого потенциала в России", "Непрерывность образования как составляющая часть человеческого потенциала Российского общества", "Региональные аспекты развития человеческого потенциала", "Социальные болезни(преступность, наркомания и терроризм) Российского общества и их влияние на развитие человеческого потенциала", "Сравнительная характеристикаиспользованиякатегории«минимальная заработная плата» в Российской Федерации и за рубежом", "Демографические характеристики, структуры семьи и рынка труда в России", "Теневая экономика в Российской Федерациии ее влияние на российский рыноктруда", "Дискриминация на рынке труда", "Механизмы существующей защиты населения в России", "Проблемы занятости уязвимых групп населения России Экономическое неравенствои бедность в России", "Реформирование российской системы образования и его влияние на рынок труда", "Культурная свобода и развитие человеческого потенциала", "Социальное партнерствои рыноктруда РФ", "Роль профсоюзов России в социально-трудовых отношениях Забастовочное движение", "Инновационный подход в профессиональномобразовании", "Воспроизводство человеческого капиталав инновационной экономике", "Сущность ивиды человеческого капитала в инновационной экономике", "Национальные проекты России и их влияние на развитие человеческого потенциала", "Имеет ли смысл человеку спорить с судьбой? (По одному или нескольким произведениям М. Ю. Лермонтова)", "Зачем человек бросает вызов судьбе? (По одному или нескольким произведениям М. Ю. Лермонтова)", "Спорить с судьбой или принимать её? (По одному или нескольким произведениям М. Ю. Лермонтова)", "Какие произведения М. Ю. Лермонтова Вы бы посоветовали прочитать другу? (По одному или нескольким произведениям М. Ю. Лермонтова)", "Лермонтов в Вашем читательском опыте. (По одному или нескольким произведениям М. Ю. Лермонтова)", "Жизнь – это поединок? (По одному или нескольким произведениям М. Ю. Лермонтова)", "Что Вам ближе в героях М. Ю. Лермонтова: стремление к одиночеству или бегство от него? (По одному или нескольким произведениям М. Ю. Лермонтова)", "Неужели зло так привлекательно? (По одному или нескольким произведениям М. Ю. Лермонтова)", "Какая из мыслей М. Ю. Лермонтова Вам ближе: «Я ищу свободы и покоя» или «Так жизнь скучна, когда боренья нет»? (По одному или нескольким произведениям М. Ю. Лермонтова)", "Что обрекает человека на одиночество? (По одному или нескольким произведениям М. Ю. Лермонтова)", "Какие поднятые М.Ю. Лермонтовым проблемы современны и сегодня? (По одному или нескольким произведениям М. Ю. Лермонтова)", "Помогает ли литература человеку познать самого себя?", "Какие нравственные уроки, с Вашей точки зрения, может преподать литература?", "Кого из литературных героев Вы узнаёте в своих современниках?", "Кого из литературных героев Вы понимаете, но не принимаете?", "Какой герой Вам ближе: созерцающий жизнь или преобразующий её?", "Какие вопросы задаёт жизни литература?", "Можно ли обойтись без книг?", "Согласны ли Вы с утверждением А. Н. Толстого: «Хорошая книга – точно беседа с умным человеком»?", "Кто из героев литературы Вам интересен и почему?", "Кто для Вас идеальный герой литературы?", "Способна ли книга сделать человека лучше?", "Как богата Россия хорошими людьми! (А. П. Чехов)", "Легко ли говорить правду?", "Возможно ли полное взаимопонимание между людьми?", "Чем страшен эгоизм?", "Трусость и предательство: как связаны эти понятия?", "Согласны ли Вы с утверждением одного из героев Д.И. Фонвизина: «Совесть… остерегает прежде, нежели судья наказывает»?", "Почему так важно научиться понимать другого?", "Почему важно уметь сострадать другому?", "Всегда ли любовь делает человека счастливым?", "Возможна ли жизнь без идеала?", "Чем опасна вседозволенность?", "Анализ глобальной политики стран определяющих ход мировой истории", "Военные и экономические блоки", "Глобализация мира за и против, моя оценка", "Яркие политики современного времени, биографии политиков.", "Тероризм в России, факты илюстрации, причины возникновения., прогнозы.", "Современные политики России, биографии, факты из жизни, аналитическая оценка", "Если бы я был презедентом России, мои реформы, законы, действия.", "Династия и история русских царей", "Президенты США, биографии, илюстрации, оценка.", "Великие войны и завоеватели", "История и сущность возникновения и действия религиозных орденов (тамплинеры, крестоносцы, массоны, иезуиты)", "Великие исторические личности,оказавшие влияние на исторический процес развития мира, биографии, фото, анализ (Александр Македонский, Платон, Наполеон, Маркс, Ленин, Гитлер, Сталин)", "Культура древнего Египта, Пирамиды, история религиии фароаонов, иероглифы письменость, фотогалерея, история мифы", "История, культура древнего Китая", "История, культура древней Индии", "История, культура древней Греции", "История древних славян (Гумелев, Асов)", "История, культура древней Иудеи, иудаизм, кабала, тора"};
            String description = "Lorem ipsum – псевдо-латинский текст, который используется для веб дизайна, типографии, оборудования, и распечатки вместо английского текста для того, чтобы сделать ударение не на содержание, а на элементы дизайна. Такой текст также называется как заполнитель. Это очень удобный инструмент для моделей (макетов). Он помогает выделить визуальные элементы в документе или презентации, например текст, шрифт или разметка. Lorem ipsum по большей части является элементом латинского текста классического автора и философа Цицерона. Слова и буквы были заменены добавлением или сокращением элементов, поэтому будет совсем неразумно пытаться передать содержание; это не гениально, не правильно, используется даже не понятный латинский. Хотя Lorem ipsum напоминает классический латинский, вы не найдете никакого смысла в сказанном. Поскольку текст Цицерона не содержит буквы K, W, или Z, что чуждо для латинского, эти буквы, а также многие другие часто вставлены в случайном порядке, чтобы скопировать тексты различных Европейских языков, поскольку диграфы не встречаются в оригинальных текстах.В профессиональной сфере часто случается так, что личные или корпоративные клиенты заказывают, чтобы публикация была сделана и представлена еще тогда, когда фактическое содержание все еще не готово. Вспомните новостные блоги, где информация публикуется каждый час в живом порядке. Тем не менее, читатели склонны к тому, чтобы быть отвлеченными доступным контентом, скажем, любым текстом, который был скопирован из газеты или интернета. Они предпочитают сконцентрироваться на тексте, пренебрегая разметкой и ее элементами. К тому же, случайный текст подвергается риску быть неумышленно смешным или оскорбительным, что является неприемлемым риском в корпоративной среде. Lorem ipsum, а также ее многие варианты были использованы в работе начиная с 1960-ых, и очень даже похоже, что еще с 16-го века.";
            List<Person> personList = personService.getByRole(Role.TEACHER);
            List<Person> adminList = personService.getByRole(Role.ADMIN);
            for (Person teacher : personList) {
                teacher = (Person) session.merge(teacher);
                List<TeacherInfo> teacherInfos = teacher.getTeacherInfos();
                List<Discipline> disciplinesTeacher = teacher.getDisciplinesTeacher();
                for (Discipline d : disciplinesTeacher) {
                    List<CourseWork> teacherCourseWorks = d.getCourseWork();
                    for (CourseWork cw : teacherCourseWorks) {
                        int countThemes = 0;
                        for (TeacherInfo ti : teacherInfos)
                            if (ti.getCourseWork().equals(cw)) {
                                countThemes = ti.getCount_theme();
                                break;
                            }
                        //Генерирует темы от 0 до количества*2
                        int generationThemesCount = rnd.nextInt(countThemes * 2 + 1);
                        for (int i = 0; i < generationThemesCount; i++) {
                            String desc = description.substring(0, rnd.nextInt(description.length()));
                            //30% Что у темы нет описания
                            if (rnd.nextDouble() < 0.30)
                                desc = "";
                            Theme theme = new Theme(desc, themes[rnd.nextInt(themes.length)], teacher);
                            //30% что тему кто то отредактировал из админов
                            if(rnd.nextDouble() < 0.30 && adminList.size() > 0)
                                theme.setText(theme.getText()+"  edited",adminList.get(rnd.nextInt(adminList.size())));

                            theme.addCourseWork(cw);

                            themeRepository.save(theme);
                        }
                    }
                }
            }
            themeRepository.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            session.close();
        }
    }
    //Привязка преподователей к дисциплинам

    public void bindTeacherToDisciplines(){
        List<User> users = userRepository.findAll();
        users.removeIf(x -> !x.getRole().getRole().equals(Role.TEACHER));
        List<Discipline> disciplineList = discinplineRepository.findAll();
        for(Discipline d:disciplineList) {
            int cnt = 0;
            for (User u : users)
                //20% что эта дисциплина будет привязна к любому преподу
                if (rnd.nextDouble() < 0.20) {
                    disciplineService.attachTeacher(u.getPerson(), d);
                    cnt++;
                }
                //Если к дисциплине не привязался не один препод, привязать вручную одного случайного
            if(cnt == 0 && users.size() > 0)
                disciplineService.attachTeacher(users.get(rnd.nextInt(users.size())).getPerson(), d);
        }
    }
    //Привязка групп к дисциплинам
    public void bindGroupsToDisciplines(){
        List<Group> list = groupRepository.findAll();
        List<Discipline> disciplineList = discinplineRepository.findAll();
        for(Discipline d:disciplineList)
            for(Group g:list)
                //50% что эта дисциплина будет группе
                if(rnd.nextDouble() < 0.50)
                    disciplineService.attachGroup(g,d);
    }
    public void createCourseworks(){
            List<Discipline> disciplineList = discinplineRepository.findAll();
            for(Discipline d:disciplineList){
                double r = rnd.nextDouble();
                int cntCourseWork;
                if(r < 0.10)
                 cntCourseWork = 0;
                else if(r < 0.30)
                    cntCourseWork = 2;
                else
                    cntCourseWork = 1;
                for(int i =0; i < cntCourseWork;i++)
                    courseworkRepository.save(new CourseWork("Курсовая по "+d.getName()+" №"+(i+1),d));
            }
        courseworkRepository.flush();
    }
    //Генерация рандомным дисциплинам(не всем) рандомных параметров
    public void createDisciplineControl(){
        List<Discipline> disciplineList  = discinplineRepository.findAll();
        for(Discipline d:disciplineList){
            //70% на создание параметров у дисциплины
            if(rnd.nextDouble() < 70){
                boolean isAutoset = rnd.nextBoolean();
                boolean isStudentChange = rnd.nextBoolean();
                boolean isStudentOffer =  rnd.nextBoolean();
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, rnd.nextInt(30)-15);
                Date begin = c.getTime();
                c.add(Calendar.DATE, rnd.nextInt(25)+3);
                Date end = c.getTime();
                controlDisciplineRepository.save(new ControlDiscipline(begin,end,isAutoset,isStudentChange,isStudentOffer,d));
            }
        }
        controlDisciplineRepository.flush();
    }

//Генерация кол-ва тем которые преподаватель должен подать к каждой привязанной к нему курсовой
    public  void createTeacherInfoCoursework() {
        SessionFactory sessionFactory =  localContainerEntityManagerFactoryBean.getObject().unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        try {
            List<Discipline> disciplineList = disciplineService.getAll();
            for (Discipline d : disciplineList) {
                d = session.get(d.getClass(), d.getId());
                List<CourseWork> courseWorks = d.getCourseWork();
                List<Group> groups = d.getGroups();
                Integer maxStudentsInGroup = 0;
                for (Group g : groups) {
                    int countStudents = g.getPersons().size();
                    if (maxStudentsInGroup < countStudents)
                        maxStudentsInGroup = countStudents;
                }
                List<Person> teachers = d.getAttachedTeachers();
                for (CourseWork cw : courseWorks) {
                    int curCount = 0;
                    for (int i = 0; i < teachers.size(); i++) {
                        int tmpCount = 0;
                        if (i == teachers.size() - 1)
                            tmpCount = maxStudentsInGroup - curCount;
                        else
                            tmpCount = rnd.nextInt(maxStudentsInGroup - curCount + 1);///warn check
                        Person t = teachers.get(i);
                        teacherInfoRepository.save(new TeacherInfo(tmpCount, t, cw));
                        curCount += tmpCount;
                    }
                }

            }
            teacherInfoRepository.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            session.close();
        }

    }
    public void createDisciplines(){
        String[] disc = new String[]{"Программирование","Архитектура вычислительных систем","Дискретная математика","Теория алгоритмов и математическая логика","Операционные системы","Программное обеспечение вычислительных систем","Алгоритмы и структуры данных","Математическая статистика","Методы вычислений","Компьютерные сети","Объектно-ориентированное программирование","Анализ данных","Базы данных и информационные системы","Теория игр","Современные средства проектирования программного обеспечения","Методы оптимизации","Защита информации","Теория программирования","Системы и методы принятия решений","Теория графов","Теория управления","Вычислительная геометрия и компьютерная графика"};
        for(String d:disc)
            discinplineRepository.save(new Discipline(d));
        discinplineRepository.flush();
    }
    public void createUsers(){
//        SessionFactory sessionFactory =  localContainerEntityManagerFactoryBean.getObject().unwrap(SessionFactory.class);
//        Session session = sessionFactory.openSession();
//        EntityManager entityManager = localContainerEntityManagerFactoryBean.getObject().createEntityManager();

            Role admin = roleRepository.findByRole(Role.ADMIN);
            Role student  = roleRepository.findByRole(Role.STUDENT);
            Role teacher = roleRepository.findByRole(Role.TEACHER);

        String[] nicknames = new String[]{"stalker","Kezan","WOGY","FEISKO","Ironfire","Alien","Lightseeker","Aria","Topmen","Alsantrius","Blackbrand","Quemal","Jay","breakingthesystem","Hellblade","Juce","Akir","Azago","Blackstalker","Hellmaster","iSlate","Vikus","Kerry","LØV€YØỮ","Reemiel","OTANO","Via"};

            userRepository.save(new User("admin",passwordEncoder.encode("admin"),admin));
            userRepository.save(new User("twobomb",passwordEncoder.encode("123456"),admin));
            userRepository.save(new User("student",passwordEncoder.encode("123456"),student));
            userRepository.save(new User("teacher",passwordEncoder.encode("123456"),teacher));

        for (String nick:nicknames) {
            //Создаение рандомной роли юзеру, 75% студент 25% препод
            Role r = rnd.nextDouble() < 0.75?student:teacher;
            userRepository.save(new User(nick, passwordEncoder.encode(nick), r));
        }
        userRepository.flush();
    }
    public void createGroups(){
        groupRepository.save(new Group(4,"1232.342.33","4ПИ"));
        groupRepository.save(new Group(3,"12342.3342.33","3ПИ"));
        groupRepository.save(new Group(4,"123.4324.545","4СА"));
        groupRepository.save(new Group(3,"123.4324.545","3СА"));
        groupRepository.flush();
    }
}
