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
flowchart LR
  classDef container stroke:#94a3b8,fill:#0b1220,stroke-width:2,color:#e5e7eb;
  classDef component stroke:#94a3b8,fill:#0f172a,stroke-dasharray:3 3,color:#e5e7eb,stroke-width:1;
  subgraph Page["Home (UI sketch)"]
    direction LR
    subgraph Left["Hero Card"]
      HTitle["Welcome to AltPro"]
      HButtons["Login | Register"]
    end
    subgraph Right["Illustration"]
      HImage["Image"]
    end
  end
  class Page,Left,Right container
  class HTitle,HButtons,HImage component
```
_Home — UI komponentų eskizas (Mermaid)_
![Home Screen](images/screen-home.png)
_Home — Realizacijos ekrano kopija_

```mermaid
flowchart LR
  classDef container stroke:#94a3b8,fill:#0b1220,stroke-width:2,color:#e5e7eb;
  classDef component stroke:#94a3b8,fill:#0f172a,stroke-dasharray:3 3,color:#e5e7eb,stroke-width:1;
  subgraph Page["Dashboard (UI sketch)"]
    direction TB
    Header["Title: Invitations & Organizations"]
    subgraph Grid["Two columns"]
      INV["Invitations list"]
      ORG["Organizations list"]
    end
  end
  class Page,Grid container
  class Header,INV,ORG component
```
_Dashboard — UI komponentų eskizas (Mermaid)_
![Dashboard Screen](images/screen-dashboard.png)
_Dashboard — Realizacijos ekrano kopija_

```mermaid
flowchart TB
  classDef container stroke:#94a3b8,fill:#0b1220,stroke-width:2,color:#e5e7eb;
  classDef component stroke:#94a3b8,fill:#0f172a,stroke-dasharray:3 3,color:#e5e7eb,stroke-width:1;
  subgraph Page["Organizations (UI sketch)"]
    Selector["Organization selector"]
    CreateForm["Create organization form"]
  end
  Selector --> CreateForm
  class Page container
  class Selector,CreateForm component
```
_Organizations — UI komponentų eskizas (Mermaid)_
![Organizations Screen](images/screen-organizations.png)
_Organizations — Realizacijos ekrano kopija_

```mermaid
flowchart TB
  classDef container stroke:#94a3b8,fill:#0b1220,stroke-width:2,color:#e5e7eb;
  classDef component stroke:#94a3b8,fill:#0f172a,stroke-dasharray:3 3,color:#e5e7eb,stroke-width:1;
  subgraph Page["Projects (UI sketch)"]
    OrgSel["Organization selector"]
    ProjList["Projects list"]
    ProjCreate["Create project form"]
  end
  OrgSel --> ProjList
  ProjList --> ProjCreate
  class Page container
  class OrgSel,ProjList,ProjCreate component
```
_Projects — UI komponentų eskizas (Mermaid)_
![Projects Screen](images/screen-projects.png)
_Projects — Realizacijos ekrano kopija_

```mermaid
flowchart TB
  classDef container stroke:#94a3b8,fill:#0b1220,stroke-width:2,color:#e5e7eb;
  classDef component stroke:#94a3b8,fill:#0f172a,stroke-dasharray:3 3,color:#e5e7eb,stroke-width:1;
  subgraph Page["Tasks (UI sketch)"]
    OrgSel["Organization selector"]
    ProjSel["Project selector"]
    subgraph Main["Content area"]
      Board["Tasks list / board"]
      TaskForm["Create / Edit task form"]
      Comments["Comments panel"]
    end
  end
  OrgSel --> ProjSel
  ProjSel --> Main
  class Page,Main container
  class OrgSel,ProjSel,Board,TaskForm,Comments component
```
_Tasks — UI komponentų eskizas (Mermaid)_
![Tasks Screen](images/screen-tasks.png)
_Tasks — Realizacijos ekrano kopija_

```mermaid
flowchart TB
  classDef container stroke:#94a3b8,fill:#0b1220,stroke-width:2,color:#e5e7eb;
  classDef component stroke:#94a3b8,fill:#0f172a,stroke-dasharray:3 3,color:#e5e7eb,stroke-width:1;
  subgraph Page["Organization Home (UI sketch)"]
    Header["Organization header"]
    Members["Members list"]
    Projects["Projects list"]
    Actions["Actions: Leave, Create Project"]
  end
  Header --> Members
  Header --> Projects
  Projects --> Actions
  class Page container
  class Header,Members,Projects,Actions component
```
_Organization Home — UI komponentų eskizas (Mermaid)_
![Organization Home Screen](images/screen-organization-home.png)
_Organization Home — Realizacijos ekrano kopija_

```mermaid
flowchart TB
  classDef container stroke:#94a3b8,fill:#0b1220,stroke-width:2,color:#e5e7eb;
  classDef component stroke:#94a3b8,fill:#0f172a,stroke-dasharray:3 3,color:#e5e7eb,stroke-width:1;
  subgraph Page["Project Settings (UI sketch)"]
    Details["Project details form"]
    Members["Members management"]
    Invite["Invite user panel"]
  end
  Details --> Members
  Members --> Invite
  class Page container
  class Details,Members,Invite component
```
_Project Settings — UI komponentų eskizas (Mermaid)_
![Project Settings Screen](images/screen-project-settings.png)
_Project Settings — Realizacijos ekrano kopija_

```mermaid
flowchart TB
  classDef container stroke:#94a3b8,fill:#0b1220,stroke-width:2,color:#e5e7eb;
  classDef component stroke:#94a3b8,fill:#0f172a,stroke-dasharray:3 3,color:#e5e7eb,stroke-width:1;
  subgraph Page["Organization Settings (UI sketch)"]
    Details["Organization details form"]
    Invitations["Invitations panel"]
    Members["Members management"]
  end
  Details --> Invitations
  Details --> Members
  class Page container
  class Details,Invitations,Members component
```
_Organization Settings — UI komponentų eskizas (Mermaid)_
![Organization Settings Screen](images/screen-organization-settings.png)
_Organization Settings — Realizacijos ekrano kopija_

```mermaid
flowchart TB
  classDef container stroke:#94a3b8,fill:#0b1220,stroke-width:2,color:#e5e7eb;
  classDef component stroke:#94a3b8,fill:#0f172a,stroke-dasharray:3 3,color:#e5e7eb,stroke-width:1;
  subgraph Page["Auto Login (UI sketch)"]
    Card["Auto-login card"]
    Status["Status message"]
  end
  Card --> Status
  class Page container
  class Card,Status component
```
_Auto Login — UI komponentų eskizas (Mermaid)_
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
