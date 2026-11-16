# Cart Merge Guard

A narrowly scoped derivative of [Shopizer](https://github.com/shopizer-ecommerce/shopizer),
based on branch `3.2.7`, commit `6a4a0a65a3408ee8f62597b51d1b3aac24b77dee`.
The upstream POM reports version 3.2.5 despite the branch name. Apache-2.0 license,
original notices, architecture and history are retained. Prepared with Codex assistance.
No claim of independent authorship of Shopizer or of personal language proficiency.

## Upstream versus this contribution

Shopizer supplies the Spring Boot e-commerce application: catalog, customers,
orders, shipping integrations, persistence and REST endpoints. This contribution
fixes one cart-merge state bug and supplies two focused regression tests, a
GitHub test workflow, this contribution record and a Chinese learning guide.

`ShoppingCartServiceImpl.mergeShoppingCarts` previously declared `duplicateFound`
outside the loop over guest lines. Once one existing attributed product matched,
that flag remained true for subsequent products. Their quantities were not added;
the guest cart was still removed at the end. With three matching products at two
units each and guest quantities of three, every result should contain five units.
The new test fails on the baseline with `expected:<5> but was:<2>`.

The fix initializes the flag independently for each incoming line. It deliberately
preserves the existing matching policy, persistence calls and same-customer shortcut.
The single-product regression protects existing behavior. The multi-product case
fails regardless of which of the matching lines the set visits first.

## Reproduce

Use JDK 17 and the upstream Maven 3.5.2 wrapper:

```sh
sh ./mvnw -B -s .github/maven-settings.xml test
sh ./mvnw -B -s .github/maven-settings.xml -pl sm-core -am \
  -Dtest=ShoppingCartMergeTest -Dsurefire.failIfNoSpecifiedTests=false test
```

The optional settings file resolves unchanged dependency coordinates through
public Maven Central. Old upstream Spring repositories returned authorization
errors and delayed the default build. It contains no credentials or local proxy.
The evaluation environment required its existing proxy and provided CA in a local
JDK truststore; TLS checks remained enabled. Those machine settings are not committed.

Baseline: all six modules passed; 61 tests reported, 40 executed, 21 skipped.
Regression before fix: two tests, one assertion failure, no errors or skips.
Final full-suite results and remote CI verification are recorded in
[PORTFOLIO_PROGRESS.md](PORTFOLIO_PROGRESS.md).

## Limits

The new tests call real merge logic with product lookup and persistence isolated
using Mockito. Existing H2/HTTP integration tests also run in the full suite, but
no new database rollback, concurrent-merge or browser checkout coverage is claimed.
The pre-existing attributed-product matching policy may need a separate domain
review; this patch does not claim to fix variant identity, stock, pricing,
quantity limits, atomicity or every cart merge edge case. Upstream skipped tests
remain skipped. No production database, payment gateway or deployment was tested.

The upstream PR review included #1116 (SKU batching and order inventory identity),
#1133 (validation), #1132 (store lookup), and #1131 (JWT errors). No patch from those
proposals was copied. Searches for cart merge and `duplicateFound` did not find
this exact fix; that is not proof that no unindexed work exists. No upstream contact.
