# AltPro projekto ataskaita
Autorius: Tauras Giedraitis, IFF-2/4

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
    - `SVEČIAS` (neprisijungęs vartotojas): gali matyti tik namų puslapį, registruotis, prisijungti
  - Papildomi funkciniai aspektai:
    - OAuth2/OIDC autentifikacija (SSO), prie API prisijungiama su `Bearer` JWT
    - Įvesties validacija ir aiškūs klaidų atsakai
    - Filtravimas ir peržiūra pagal organizaciją/projektą
    - Konfigūruojama per aplinkos kintamuosius (`MONGO_*`, `issuer-uri`)
- Pasirinktų technologijų aprašymas:
  - Klientas: `React` + `TypeScript` + `Vite`
  - API (Resource Server): `Spring Boot`, `Spring Security OAuth2 Resource Server`, `MongoDB`, REST (JSON over HTTPS)
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
        API["AltPro API Service\nSpring Boot (Resource Server, REST JSON)"]
        MongoDB[("MongoDB Database")]
    end

    Browser ---|"HTTPS — REST requests via Nginx"| Nginx
    Nginx ---|"Proxy — routes /auth/* (OIDC) to AltPro Auth"| Auth
    Nginx ---|"Proxy — routes /api/* (REST JSON) to AltPro API"| API
    Auth ---|"Stores users, clients, consents"| MongoDB
    API ---|"Stores organizations, projects, tasks, comments"| MongoDB
    Browser ---|"OIDC — redirects for login/logout (Auth Code + PKCE)"| Auth
    Browser ---|"Bearer JWT — REST calls to resource API"| API
```

- Diagramos paaiškinimas:
  - Klientas per `Nginx` jungiasi prie dviejų paslaugų: `AltPro Auth` (SSO) ir `AltPro API` (resursų serveris).
  - Abi paslaugos naudoja tą pačią `MongoDB` duomenų bazę: Auth saugo naudotojus, klientus ir sutikimus; API — domeno duomenis (organizacijos, projektai, užduotys, komentarai).
  - Naršyklė gauna OIDC identiteto žetoną, o prie API prisijungia su `Bearer` JWT.

## 3. Naudotojo sąsajos projektas
- Žemiau pateikiama kiekvieno lango pora: pirma „wireframe“ (Mermaid diagrama), po to atitinkama realizacijos ekrano kopija.

```mermaid
flowchart TD
  subgraph Home["Home"]
    H1["Hero: Welcome to AltPro"]
    CTA["Buttons: Login | Register"]
    IMG["Illustration"]
  end
  H1 --> CTA
  H1 --> IMG
```
_Home — Wireframe (Mermaid)_
![Home Screen](images/screen-home.png)
_Home — Realizacijos ekrano kopija_

```mermaid
flowchart TD
  subgraph Dashboard["Dashboard"]
    INV["Invitations list"]
    ORG["Organizations list"]
  end
  INV --- ORG
```
_Dashboard — Wireframe (Mermaid)_
![Dashboard Screen](images/screen-dashboard.png)
_Dashboard — Realizacijos ekrano kopija_

```mermaid
flowchart TD
  subgraph Organizations["Organizations"]
    SEL["Organization selector"]
    CREATE["Create organization form"]
  end
  SEL --> CREATE
```
_Organizations — Wireframe (Mermaid)_
![Organizations Screen](images/screen-organizations.png)
_Organizations — Realizacijos ekrano kopija_

```mermaid
flowchart TD
  subgraph Projects["Projects"]
    ORGSEL["Organization selector"]
    LIST["Projects list"]
    CREATE["Create project form"]
  end
  ORGSEL --> LIST
  LIST --> CREATE
```
_Projects — Wireframe (Mermaid)_
![Projects Screen](images/screen-projects.png)
_Projects — Realizacijos ekrano kopija_

```mermaid
flowchart TD
  subgraph Tasks["Tasks"]
    ORGSEL["Organization selector"]
    PROJSEL["Project selector"]
    BOARD["Tasks list/board"]
    FORM["Create/Edit task form"]
    COMMS["Comments panel"]
  end
  ORGSEL --> PROJSEL
  PROJSEL --> BOARD
  BOARD --- FORM
  BOARD --- COMMS
```
_Tasks — Wireframe (Mermaid)_
![Tasks Screen](images/screen-tasks.png)
_Tasks — Realizacijos ekrano kopija_

```mermaid
flowchart TD
  subgraph OrgHome["Organization Home"]
    HEADER["Organization header"]
    MEMBERS["Members list"]
    PROJECTS["Projects list"]
    ACTIONS["Actions: Leave, Create Project"]
  end
  HEADER --> MEMBERS
  HEADER --> PROJECTS
  PROJECTS --> ACTIONS
```
_Organization Home — Wireframe (Mermaid)_
![Organization Home Screen](images/screen-organization-home.png)
_Organization Home — Realizacijos ekrano kopija_

```mermaid
flowchart TD
  subgraph ProjectSettings["Project Settings"]
    DETAILS["Project details form"]
    MEMBERS["Members management"]
    INVITE["Invite user panel"]
  end
  DETAILS --> MEMBERS
  MEMBERS --> INVITE
```
_Project Settings — Wireframe (Mermaid)_
![Project Settings Screen](images/screen-project-settings.png)
_Project Settings — Realizacijos ekrano kopija_

```mermaid
flowchart TD
  subgraph OrgSettings["Organization Settings"]
    DETAILS["Organization details form"]
    INVITES["Invitations panel"]
    MEMBERS["Members management"]
  end
  DETAILS --> INVITES
  DETAILS --> MEMBERS
```
_Organization Settings — Wireframe (Mermaid)_
![Organization Settings Screen](images/screen-organization-settings.png)
_Organization Settings — Realizacijos ekrano kopija_

```mermaid
flowchart TD
  subgraph AutoLogin["Auto Login"]
    CARD["Auto-login card"]
    STATUS["Status message"]
  end
  CARD --> STATUS
```
_Auto Login — Wireframe (Mermaid)_
![Auto Login Screen](images/screen-auto-login.png)
_Auto Login — Realizacijos ekrano kopija_

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
