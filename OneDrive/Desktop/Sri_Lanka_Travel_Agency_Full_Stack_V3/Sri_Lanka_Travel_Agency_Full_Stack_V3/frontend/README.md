# Serendib Trails React Frontend

React 18 + Vite frontend for the Spring Boot API in the sibling `travel/`
folder.

## Run

```powershell
cd frontend
Copy-Item .env.example .env
npm install
npm run dev
```

Open `http://localhost:5173`.

The default API URL is `http://localhost:8080`. Change `VITE_API_BASE_URL` in
`.env` only when the backend runs elsewhere.

## Included routes

### Customer

- `/` — animated home page
- `/about`, `/contact`
- `/destinations`, `/destinations/:id`
- `/packages`, `/packages/:id`
- `/login`, `/register`
- `/booking/:packageId`, `/my-bookings`

### Admin

- `/admin/dashboard`
- `/admin/destinations`
- `/admin/packages`
- `/admin/inquiries`
- `/admin/bookings`
- `/admin/users`

Theme preference and JWT session are stored in browser local storage. The API
still enforces every ADMIN/CUSTOMER authorization rule on the server.

The destination screen supports `search`, `district`, `category` and
`featured` URL filters. The complete initial catalogue is loaded through the
Spring Boot backend and managed from `/admin/destinations`.
