# AltPro projekto ataskaita
_Autorius: Tauras Giedraitis, IFF-2/4_

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
  - API (Resource Server): `Spring Boot`, `Spring Security OAuth2 Resource Server`, `MongoDB`, RESTful API (JSON over HTTPS)
  - Autorizacija (Authorization Server): `Spring Boot`, `Spring Authorization Server`, `MongoDB`
  - Tarpinis sluoksnis: `Nginx` reverse proxy
  - Diegimo aplinka: `Ubuntu VPS`

## 2. Sistemos architektūra
- Diegimo diagrama:

```mermaid
flowchart TD
    subgraph Client_Device["Computer Device with Browser"]
        Browser["Browser Artifact\nAltPro Web (React + TypeScript)"]
    end

    subgraph Ubuntu_VPS["Ubuntu VPS Device"]
        Nginx["Nginx Reverse Proxy"]
        Auth["AltPro Auth Service\nSpring Boot (OAuth2 Authorization Server)"]
        API["AltPro API Service\nSpring Boot (Resource Server, RESTful API)"]
        MongoDB[("MongoDB Database")]
    end

    Browser ---|"HTTPS — RESTful requests via Nginx"| Nginx
    Nginx ---|"Proxy — routes /auth/* to AltPro Auth"| Auth
    Nginx ---|"Proxy — routes /api/* (RESTful JSON) to AltPro API"| API
    Auth ---|"Stores users, clients, consents"| MongoDB
    API ---|"Stores organizations, projects, tasks, comments"| MongoDB
    Browser ---|"OIDC — redirects for login/logout"| Auth
    Browser ---|"Bearer JWT — RESTful calls to resource API"| API
```

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

![Organization Create Wireframe](images/wireframe-organization-create.png)
_Organization Create — Wireframe_
![Organization Create Screen](images/screen-organization-create.png)
_Organization Create — Realizacijos ekrano kopija_

![Organization Home Wireframe](images/wireframe-organization-home.png)
_Organization Home — Wireframe_
![Organization Home Screen](images/screen-organization-home.png)
_Organization Home — Realizacijos ekrano kopija_

![Organization Settings Wireframe](images/wireframe-organization-settings.png)
_Organization Settings — Wireframe_
![Organization Settings Screen](images/screen-organization-settings.png)
_Organization Settings — Realizacijos ekrano kopija_

![Project Create Wireframe](images/wireframe-project-create.png)
_Project Create — Wireframe_
![Project Create Screen](images/screen-project-create.png)
_Project Create — Realizacijos ekrano kopija_

![Project Home Wireframe](images/wireframe-project-home.png)
_Project Home — Wireframe_
![Project Home Screen](images/screen-project-home.png)
_Project Home — Realizacijos ekrano kopija_

![Project Settings Wireframe](images/wireframe-project-settings.png)
_Project Settings — Wireframe_
![Project Settings Screen](images/screen-project-settings.png)
_Project Settings — Realizacijos ekrano kopija_

![Task Create Wireframe](images/wireframe-task-create.png)
_Task Create — Wireframe_
![Task Create Screen](images/screen-task-create.png)
_Task Create — Realizacijos ekrano kopija_

![Task Details Wireframe](images/wireframe-task-details.png)
_Task Details — Wireframe_
![Task Details Screen](images/screen-task-details.png)
_Task Details — Realizacijos ekrano kopija_

## OpenAPI specifikacija
OpenAPI specifikacijos failą (api-spec.yaml) galima rasti projekto repozitorijoje:
`./api-spec.yaml`

### API pagrindai
- **Base URL**: `http://localhost:9001` (development) / `https://api.altpro.com` (production)
- **Autentifikacija**: Bearer token (JWT)
- **Formatas**: JSON (RESTful)
- **HTTP metodai**: GET, POST, PUT, DELETE

### Pagrindiniai endpoint'ai
#### Organizacijos (`/api/orgs`)
```
POST   /api/orgs              - Sukurti organizaciją
GET    /api/orgs              - Gauti vartotojo organizacijas
GET    /api/orgs/{id}         - Gauti organizaciją pagal ID
PUT    /api/orgs/{id}         - Atnaujinti organizaciją
DELETE /api/orgs/{id}         - Ištrinti organizaciją
POST   /api/orgs/{id}/members - Pridėti narį
PUT    /api/orgs/{id}/members/{memberId} - Atnaujinti nario rolę
DELETE /api/orgs/{id}/members/{memberId} - Pašalinti narį
```

#### Projektai (`/api/orgs/{orgId}/projects`)
```
POST   /api/orgs/{orgId}/projects        - Sukurti projektą
GET    /api/orgs/{orgId}/projects        - Gauti organizacijos projektus
GET    /api/orgs/{orgId}/projects/{id}   - Gauti projektą pagal ID
PUT    /api/orgs/{orgId}/projects/{id}   - Atnaujinti projektą
DELETE /api/orgs/{orgId}/projects/{id}   - Ištrinti projektą
```

#### Užduotys (`/api/orgs/{orgId}/tasks`)
```
POST   /api/orgs/{orgId}/tasks                    - Sukurti užduotį
GET    /api/orgs/{orgId}/tasks                    - Gauti organizacijos užduotis
GET    /api/orgs/{orgId}/tasks/{id}               - Gauti užduotį pagal ID
PUT    /api/orgs/{orgId}/tasks/{id}               - Atnaujinti užduotį
DELETE /api/orgs/{orgId}/tasks/{id}               - Ištrinti užduotį
GET    /api/orgs/{orgId}/tasks/project/{projectId} - Gauti projekto užduotis
```

#### Komentarai (`/api/orgs/{orgId}/comments`)
```
POST   /api/orgs/{orgId}/comments                 - Sukurti komentarą
GET    /api/orgs/{orgId}/comments                 - Gauti organizacijos komentarus
GET    /api/orgs/{orgId}/comments/{id}            - Gauti komentarą pagal ID
PUT    /api/orgs/{orgId}/comments/{id}            - Atnaujinti komentarą
DELETE /api/orgs/{orgId}/comments/{id}            - Ištrinti komentarą
GET    /api/orgs/{orgId}/comments/task/{taskId}   - Gauti užduoties komentarus
```

### API naudojimo pavyzdžiai
#### Organizacijos kūrimas
```bash
POST /api/orgs
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "name": "Mano Kompanija",
  "description": "Projektų valdymo organizacija"
}
```

**Atsakymas (201 Created)**:
```json
{
  "id": "64f1a2b3c4d5e6f7g8h9i0j",
  "name": "Mano Kompanija",
  "description": "Projektų valdymo organizacija",
  "createdAt": "2024-12-21T10:00:00Z",
  "members": [
    {
      "userId": "user123",
      "role": "ADMIN"
    }
  ]
}
```

#### Užduoties kūrimas
```bash
POST /api/orgs/64f1a2b3c4d5e6f7g8h9i0j/tasks
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "title": "Implementuoti vartotojo autentifikaciją",
  "description": "Pridėti OAuth2 integraciją su JWT tokens",
  "status": "TODO",
  "priority": 4,
  "assignee": "user456"
}
```

**Atsakymas (201 Created)**:
```json
{
  "id": "64f1a2b3c4d5e6f7g8h9i0k",
  "projectId": "64f1a2b3c4d5e6f7g8h9i0l",
  "title": "Implementuoti vartotojo autentifikaciją",
  "description": "Pridėti OAuth2 integraciją su JWT tokens",
  "status": "TODO",
  "priority": 4,
  "createdAt": "2024-12-21T11:30:00Z",
  "assignee": "user456"
}
```

### Galimi atsakymų kodai
- **200 OK** - Sėkmingas užklausa
- **201 Created** - Resursas sukurtas
- **204 No Content** - Resursas ištrintas
- **400 Bad Request** - Klaidingi duomenys
- **401 Unauthorized** - Neautorizuota užklausa
- **403 Forbidden** - Nepakanka teisių
- **404 Not Found** - Resursas nerastas
- **500 Internal Server Error** - Serverio klaida

## 5. Projekto išvados
### Pagrindiniai pasiekimai:
- Klientas, API ir autorizacija veikia atskiruose serveriuose (per `Nginx`).
- Įdiegta OAuth2/OIDC autentifikacija ir autorizacija su JWT (Access tokens, PKCE).
- Sukurta RESTful API su pilnu CRUD funkcionalumu (organizacijos, projektai, užduotys, komentarai).
- Hierarchiniai endpoint’ai atitinka ryšius (`/api/orgs/{orgId}/projects/...`).
- Integruota `MongoDB` duomenų bazė ir validacija.
- API dokumentuota su OpenAPI 3.0 (`api-spec.yaml`, suderinama su `springdoc`).

### Panaudotos technologijos:
- Frontend: React 18 + TypeScript + Vite
- Backend (Resource Server): Spring Boot 3.5 + Spring Security + Spring Data MongoDB
- Autorizacija: Spring Authorization Server (OAuth2/OIDC)
- Duomenų bazė: MongoDB
- API dokumentacija: OpenAPI 3.0 (springdoc UI)
