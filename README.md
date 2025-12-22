# AltPro

## Apžvalga
**AltPro** – projektų planavimo ir užduočių sekimo sistema, skirta komandoms ir individualiems naudotojams. Ji leidžia:  
- Kurti projektus ir užduotis, priskirti atsakingus asmenis.  
- Stebėti darbų progresą realiuoju laiku.  
- Bendradarbiauti per komentarus ir dalintis informacija.  

Sistema sujungia populiarių įrankių („Jira“, „Trello“, „ClickUp“) funkcijas į vieną platformą. Visi naudotojai prisijungia per **Alternative Account**, centralizuotą autentifikacijos sprendimą (panašų į Google/Microsoft Account).

---

## Pagrindiniai objektai
1. **Projektas** – turi pavadinimą, aprašymą, sukūrimo datą, priskirtus vartotojus ir užduotis.  
2. **Užduotis** – priklauso projektui, turi būseną, prioritetą, gali būti suskaidyta į subtasks.  
3. **Komentaras** – priklauso užduočiai, leidžia bendrauti ir palikti pastabas.

---

## API funkcionalumas
Kiekvienam objektui realizuojami šie metodai:  
- **Create** – sukurti objektą  
- **Read (by ID)** – gauti objektą pagal ID  
- **Update** – atnaujinti objektą  
- **Delete** – pašalinti objektą  
- **List** – gauti visų objektų sąrašą  

Hierarchiniai metodai:  
- `GET /projects/{id}/tasks` – grąžina projekto užduotis  
- `GET /tasks/{id}/comments` – grąžina užduoties komentarus

---

## Naudotojų rolės
- **Administratorius** – pilnos teisės, gali valdyti projektus, narius ir prieigos teises  
- **Komandos narys** – gali redaguoti priskirtas užduotis ir rašyti komentarus  
- **Svečias** – tik skaitymo teisės

---

## Technologijos
- **Duomenų bazė:** MongoDB  
- **Backend:** Spring Boot, REST API  
- **Frontend:** React  
- **Autentifikacija:** OAuth2 per Alternative Account  
- **Debesų sprendimas:** Ubuntu serveris, Nginx reverse proxy, Docker arba kita procesų valdymo sistema