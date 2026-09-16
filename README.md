# Chumz Clone

A Spring Boot + Thymeleaf clone of [Chumz](https://chumz.co.ke), the Kenyan
micro-savings app — built as an academic/diploma project. Neumorphic
("soft UI") design throughout.

## Stack
- Java 17, Spring Boot 3.3
- Spring MVC + Thymeleaf (server-rendered views)
- Spring Data JPA + PostgreSQL
- Spring Security (form login)
- WebClient for Safaricom Daraja (M-Pesa STK Push) sandbox integration

## Features
- **Auth** — register/login (BCrypt-hashed passwords)
- **Savings goals** — create goals with a target amount/date, deposit via
  M-Pesa STK push, track progress with a neumorphic progress bar
- **Group savings** — create a group, get a shareable invite code, invite
  members, everyone contributes and sees the pooled total
- **Challenges** — 52-week envelope challenge, round-up challenge, daily
  fixed challenge (seeded on first run)
- **Streaks** — consecutive-day saving streak tracked automatically on
  every successful deposit/contribution
- **Reports** — total saved, per-transaction history

## Getting started

1. **Create the database**
   ```sql
   CREATE DATABASE chumz_clone;
   ```

2. **Set your PostgreSQL credentials** in
   `src/main/resources/application.properties`
   (`spring.datasource.username` / `password`).

3. **(Optional) Daraja sandbox credentials** — for real STK push prompts:
   - Register at https://developer.safaricom.co.ke
   - Create an app, grab the Consumer Key/Secret
   - Use the shared sandbox shortcode `174379` and its test passkey
   - Expose your local server with `ngrok http 8080` and put the ngrok
     URL + `/api/mpesa/callback` into `daraja.callback-url`

   Without valid credentials, deposits/contributions still work — the
   app falls back to an instant "sandbox mode" credit so the whole flow
   is demoable offline (see `GoalController` / `GroupController`).

4. **Run it**
   ```bash
   ./mvnw spring-boot:run
   ```
   Visit http://localhost:8080

## Project layout
```
entity/       JPA entities (User, SavingsGoal, SavingsGroup, GroupMember,
              Transaction, Challenge, UserChallenge)
repository/   Spring Data repositories
service/      Business logic (UserService, SavingsGoalService,
              SavingsGroupService, DarajaService, StreakService,
              ChallengeService)
controller/   MVC controllers + the M-Pesa callback REST endpoint
config/       Security config, WebClient bean, demo data seeder
templates/    Thymeleaf views (neumorphic CSS in static/css/neumorphism.css)
```

## Known simplifications (documented for the write-up)
- CSRF is disabled for simplicity in the academic sandbox — re-enable
  with hidden tokens in forms for a production build.
- The Daraja callback doesn't yet re-associate `CheckoutRequestID` with
  the pending goal/group (Safaricom's sandbox doesn't echo
  `AccountReference` in callback metadata) — a production version should
  persist a `PENDING` `Transaction` at STK-push time, keyed by
  `checkoutRequestId`, and update it in the callback instead of trusting
  client-side confirmation.
- No email verification / password reset flow.
