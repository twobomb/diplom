package com.twobomb.app;

import com.twobomb.entity.*;
import com.twobomb.repository.*;
import com.twobomb.service.DisciplineService;
import com.twobomb.service.UserService;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.hql.internal.QueryTranslatorFactoryInitiator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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
    Random rnd;

    public DataGeneration(UserRepository userRepository,GroupRepository groupRepository,RoleRepository roleRepository,PersonRepository personRepository,PasswordEncoder passwordEncoder,DiscinplineRepository discinplineRepository,CourseworkRepository courseworkRepository,DisciplineService disciplineService) {
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.personRepository = personRepository;
    this.discinplineRepository = discinplineRepository;
    this.courseworkRepository = courseworkRepository;
    this.disciplineService = disciplineService;
    rnd  = new Random();
    }

    @Autowired
    LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;


    @PostConstruct
    public void create(){

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
    //Привязка преподователей к дисциплинам

    public void bindTeacherToDisciplines(){
        List<User> users = userRepository.findAll();
        users.removeIf(x -> !x.getRole().getRole().equals(Role.TEACHER));
        List<Discipline> disciplineList = discinplineRepository.findAll();
        for(Discipline d:disciplineList)
            for(User u:users)
                //20% что эта дисциплина будет привязна к любому преподу
                if(rnd.nextDouble() < 0.20)
                    disciplineService.attachTeacher(u.getPerson(),d);
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
