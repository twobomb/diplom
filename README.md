# diplom
<h1>Дипломная работа</h1>
<h3>Распределение дипломных и курсовых работ</h3>
<h5>Используемые технологии</h5>
<ul>
<li>Spring</li>
<li>-Vaadin 10(Flow)</li>
<li>-JPA\Hibernate</li>
<li>-Polymer</li>
<li>-PostgreSQL</li>
</ul>
Файл application.properties - содержит настройки БД и Hibernate.<br>
 Класс DataGeneration - генерирует случайные данные в бд если в <code>applcation.properties</code> значение <code>spring.jpa.properties.hibernate.hbm2ddl.auto </code> не равно update. А например create и т.п.
