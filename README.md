# AltPro projekto ataskaita

## 1. Sprendžiamo uždavinio aprašymas
- Sistemos paskirtis:
  - „AltPro“ skirta organizacijų projektų ir užduočių valdymui, užtikrinant vieno prisijungimo (SSO) autentifikaciją per atskirą autorizacijos serverį.
  - Naudotojai kuria organizacijas, projektus, užduotis, kviečia narius ir dirba vienoje vietoje su centralizuota prieiga.
- Funkciniai reikalavimai:
  - Taikomosios srities objektai:
    - `Organization` (pavadinimas, aprašymas, nariai)
    - `Project` (priklauso organizacijai, pavadinimas, aprašymas, nariai)
    - `Task` (priklauso projektui, pavadinimas, aprašymas, būsena, prioritetas)
    - `Invitation` (kvietimas prisijungti prie organizacijos)
    - `Comment` (komentaras prie užduoties)
  - API metodai:
    - Organizacijos: sąrašas, kūrimas, peržiūra, atnaujinimas, narių valdymas, kvietimų peržiūra, palikimas
    - Projektai: sąrašas, kūrimas, peržiūra, atnaujinimas, šalinimas
    - Užduotys: sąrašas, kūrimas, peržiūra, atnaujinimas, šalinimas, filtravimas pagal projektą
    - Kvietimai: mano kvietimai, kvietimo priėmimas / atmetimas
    - Komentarai: sąrašas pagal užduotį
  - Hierarchinis API metodas:
    - Pvz. `/api/orgs/{orgId}/projects/{projectId}/tasks` (užduotys konkrečiame organizacijos projekte)
  - Naudotojų rolės:
    - `ADMIN` (organizacijos administratorius): valdo narius, gali kurti/šalinti projektus, keisti nustatymus
    - `MEMBER` (organizacijos narys): gali kurti ir tvarkyti užduotis, komentuoti, dalyvauti projektuose
  - Papildomi funkciniai aspektai:
    - OAuth2/OIDC autentifikacija (SSO), prie API prisijungiama su `Bearer` JWT
    - Įvesties validacija ir aiškūs klaidų atsakai
    - Filtravimas ir peržiūra pagal organizaciją/projektą
    - Konfigūruojama per aplinkos kintamuosius (`MONGO_*`, `issuer-uri`)
- Pasirinktų technologijų aprašymas:
  - Klientas: `React` + `TypeScript` + `Vite`
  - API (Resource Server): `Spring Boot`, `Spring Security OAuth2 Resource Server`, `MongoDB`
  - Autorizacija (Authorization Server): `Spring Boot`, `Spring Authorization Server`, `MongoDB`
  - Tarpinis sluoksnis: `Nginx` reverse proxy
  - Diegimo aplinka: `Ubuntu VPS`

## 2. Sistemos architektūra
- Diegimo diagrama (UML principu, English):

```mermaid
flowchart TD
    subgraph Client_Device["Computer Device with Browser"]
        Browser["Browser Artifact\nAltPro Web (React + TypeScript)"]
    end

    subgraph Ubuntu_VPS["Ubuntu VPS Device"]
        Nginx["Nginx Reverse Proxy"]
        Auth["AltPro Auth Service\nSpring Boot (OAuth2 Authorization Server)"]
        API["AltPro API Service\nSpring Boot (Resource Server)"]
        MongoDB[("MongoDB Database")]
    end

    Browser ---|"HTTPS — requests via Nginx"| Nginx
    Nginx ---|"Proxy — routes /auth/* to AltPro Auth"| Auth
    Nginx ---|"Proxy — routes /api/* to AltPro API"| API
    Auth ---|"Stores users, clients, consents"| MongoDB
    API ---|"Stores organizations, projects, tasks, comments"| MongoDB
    Browser ---|"OIDC — redirects for login/logout"| Auth
    Browser ---|"Bearer JWT — calls resource API"| API
```

![Deployment Diagram — AltPro (English)](images/deployment-diagram.png)
_Deployment Diagram — AltPro (English)_

- Diagramos paaiškinimas:
  - Klientas per `Nginx` jungiasi prie dviejų paslaugų: `AltPro Auth` (SSO) ir `AltPro API` (resursų serveris).
  - Abi paslaugos naudoja tą pačią `MongoDB` duomenų bazę: Auth saugo naudotojus, klientus ir sutikimus; API — domeno duomenis (organizacijos, projektai, užduotys, komentarai).
  - Naršyklė gauna OIDC identiteto žetoną, o prie API prisijungia su `Bearer` JWT.

## 3. Naudotojo sąsajos projektas
- Žemiau pateikiama kiekvieno lango pora: pirma „wireframe“, po to atitinkama realizacijos ekrano kopija.

![Home Wireframe](images/wireframe-home.png)
_Home — Wireframe_
![Home Screen](images/screen-home.png)
_Home — Realizacijos ekrano kopija_

![Dashboard Wireframe](images/wireframe-dashboard.png)
_Dashboard — Wireframe_
![Dashboard Screen](images/screen-dashboard.png)
_Dashboard — Realizacijos ekrano kopija_

![Organizations Wireframe](images/wireframe-organizations.png)
_Organizations — Wireframe_
![Organizations Screen](images/screen-organizations.png)
_Organizations — Realizacijos ekrano kopija_

![Projects Wireframe](images/wireframe-projects.png)
_Projects — Wireframe_
![Projects Screen](images/screen-projects.png)
_Projects — Realizacijos ekrano kopija_

![Tasks Wireframe](images/wireframe-tasks.png)
_Tasks — Wireframe_
![Tasks Screen](images/screen-tasks.png)
_Tasks — Realizacijos ekrano kopija_

![Organization Home Wireframe](images/wireframe-organization-home.png)
_Organization Home — Wireframe_
![Organization Home Screen](images/screen-organization-home.png)
_Organization Home — Realizacijos ekrano kopija_

![Project Settings Wireframe](images/wireframe-project-settings.png)
_Project Settings — Wireframe_
![Project Settings Screen](images/screen-project-settings.png)
_Project Settings — Realizacijos ekrano kopija_

![Organization Settings Wireframe](images/wireframe-organization-settings.png)
_Organization Settings — Wireframe_
![Organization Settings Screen](images/screen-organization-settings.png)
_Organization Settings — Realizacijos ekrano kopija_

![Auth Callback Wireframe](images/wireframe-auth-callback.png)
_Auth Callback — Wireframe_
![Auth Callback Screen](images/screen-auth-callback.png)
_Auth Callback — Realizacijos ekrano kopija_

![Auto Login Wireframe](images/wireframe-auto-login.png)
_Auto Login — Wireframe_
![Auto Login Screen](images/screen-auto-login.png)
_Auto Login — Realizacijos ekrano kopija_

![Logout Wireframe](images/wireframe-logout.png)
_Logout — Wireframe_
![Logout Screen](images/screen-logout.png)
_Logout — Realizacijos ekrano kopija_

## OpenAPI specifikacija

OpenAPI specifikacijos failą (api-spec.yaml) galima rasti projekto repozitorijoje:
`./api-spec.yaml`

API dokumentacijos pavyzdys
GET `/api/orgs` — Gauti visas organizacijas

Responses:
- 200 OK — Grąžina organizacijų sąrašą
- 401 Unauthorized — Vartotojas neautentifikuotas
- 403 Forbidden — Neturi prieigos teisių
- 500 Internal Server Error — Serverio klaida

Response Schema (Organization):

```json
{
  "id": "org_123",
  "name": "KTU Dev Club",
  "description": "Student projects organization",
  "createdAt": "2025-10-01T10:00:00Z",
  "members": [
    { "userId": "user1", "role": "ADMIN" },
    { "userId": "user2", "role": "MEMBER" }
  ]
}
```

GET `/api/orgs/{id}` — Gauti vieną organizaciją

Parameters:
- `id` (path, required) — Organizacijos ID (string)

Responses:
- 200 OK — Grąžina organizacijos informaciją
- 404 Not Found — Organizacija nerasta
- 401 Unauthorized — Vartotojas neautentifikuotas
- 403 Forbidden — Neturi prieigos teisių

## 5. Projekto išvados
- Atskyrus autorizacijos serverį nuo resursų serverio, pasiekiamas saugus ir lankstus SSO.
- Hierarchinis API dizainas (`/api/orgs/{orgId}/projects/...`) natūraliai atspindi domeno ryšius ir supaprastina teisių taikymą.
- `MongoDB` tinka greitam prototipavimui ir dokumentiniams duomenims; esant poreikiui, galima keisti saugyklą.
- `React + TypeScript + Vite` leidžia kurti greitą ir tipais saugų klientą.
- `Nginx` centralizuoja srautą ir palengvina TLS, maršrutizavimą bei mastelį.
