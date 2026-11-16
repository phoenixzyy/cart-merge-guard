# Portfolio verification record

## 2026-11-17 — Cart Merge Guard (Shopizer)

- Pinned upstream 3.2.7 branch at 6a4a0a65a3408ee8f62597b51d1b3aac24b77dee
  (POM version 3.2.5), Apache-2.0 retained. Read README contribution guidance;
  no AGENTS.md or skill files were found in this checkout. No upstream contact.
- Environment investigation found no other selected execution environment.
  Installed official Temurin JDK 17.0.21+9 (SHA256 verified) and upstream-pinned
  Maven 3.5.2. Used existing environment proxy and CA with TLS verification.
  Java compiler and Maven Central downloads now work. alf.io remains paused
  because a supported Docker/Podman runtime for its PostgreSQL tests is absent.
- Default repository resolution first failed CA validation, then timed out after
  240 seconds after CA correction. Official Central-only settings succeeded with
  unchanged dependency versions. No credentials, proxy details or CA bundled.
- Unmodified full reactor baseline: six modules SUCCESS; sm-core 28 tests / 13
  skipped, sm-shop 33 / 8 skipped. Total 61 reported, 40 executed, 21 skipped,
  zero failures/errors. Baseline log retained outside the repository.
- Reproduced per-line merge-state loss: three matched products, each quantity
  2 + 3; old code leaves later lines at 2. New two-test suite against old code:
  one failure (expected 5, actual 2), one pass, zero errors/skips.
- Fix resets duplicateFound for each incoming line. Final full reactor: six
  modules SUCCESS; sm-core 30 / 13 skipped, sm-shop 33 / 8 skipped. Total 63
  reported, 42 executed, 21 skipped; zero failures/errors. New tests use real
  merge logic with product lookup and persistence mocked. Existing H2/HTTP
  integration suite passed; no claim of new DB atomicity or browser E2E coverage.
- Normal GitHub Fork created as https://github.com/phoenixzyy/cart-merge-guard .
  Identity verified phoenixzyy ID 7067843; UI confirms forked from
  shopizer-ecommerce/shopizer, 383 branches and 24 tags preserved.
- Remote commit/CI validation pending at initial publication; see later entry.
  No deployment, purchase, upstream message, Issue or PR. Other repos untouched.
- Other read-only candidate: Teedy (sismics/docs), GPL-2.0 Java document-management
  application with embedded H2, Jersey/Jetty rather than Spring Boot; no build or
  Fork attempted after Shopizer's baseline succeeded.
- hook-deadline run 43923772086: five Linux/Windows jobs succeeded; macOS job
  133497650426 remained queued at the latest check. Not claimed fully complete.
