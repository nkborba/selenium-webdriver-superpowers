# Selenium WebDriver Superpowers

Portfolio test automation project demonstrating **Selenium 4 capabilities that are distinctive** compared to Cypress and Playwright — multi-tab orchestration, native window handles, frame switching, browser dialogs, relative locators, and the shared WebDriver foundation used by Appium.

## Stack

| Tool | Version |
|------|---------|
| Java | 21 |
| Selenium | 4.45.0 |
| TestNG | 7.12.0 |
| Maven | 3.x |
| Browser | Chrome (driver resolved by **Selenium Manager**) |

## Why Selenium for these scenarios?

Cypress and Playwright are excellent modern tools, but they optimize for a **single browser context** with built-in retry and network interception. Selenium speaks the **W3C WebDriver protocol** directly to the browser — that lower-level control is what makes it the common foundation for cross-browser grids, legacy stacks, and mobile automation via Appium.

This project keeps tests readable and avoids Page Objects or factories. Each test highlights one WebDriver capability with a short comment explaining the trade-off.

## Tests

### `ResearchFlowTest` — Wikipedia research flow

1. Search for **Selenium (software)** on wikipedia.org
2. Assert the article heading
3. Scroll with the **Actions API**
4. Open the **WebDriver** section and **Cypress (software)** in **new tabs** via `switchTo().newWindow(TAB)`
5. Switch handles, extract each heading and first paragraph, close extra tabs, return to the original window

> Note: Wikipedia’s `/wiki/WebDriver` redirect merges into the Selenium article, so the WebDriver tab opens the `#Selenium_WebDriver` section directly.

### `SeleniumSuperpowersTest` — the-internet.herokuapp.com

| Test | Page | Technique |
|------|------|-----------|
| `windowsPageOpensAndClosesSecondaryWindow` | `/windows` | Window handle switching |
| `loginWithRelativeLocator` | `/login` | Selenium 4 **relative locator** (`below`) |
| `nestedFrames` | `/nested_frames` | Nested `switchTo().frame()` |
| `javascriptConfirmAlert` | `/javascript_alerts` | `switchTo().alert()` |
| `dynamicLoadingExplicitWait` | `/dynamic_loading/1` | `WebDriverWait` for async content |

## Run

```bash
# Headed (default) — watch the browser
mvn test

# Headless CI / background run
mvn test -Dheadless=true

# Single class
mvn test -Dtest=ResearchFlowTest
mvn test -Dtest=SeleniumSuperpowersTest
```

Requires **Chrome** installed locally. Selenium Manager downloads the matching ChromeDriver automatically — no WebDriverManager dependency.

## Selenium vs Cypress vs Playwright

| Capability | Selenium | Cypress | Playwright |
|------------|----------|---------|------------|
| **Multi-tab / multi-window** | Native `getWindowHandles()`, `switchTo().window()`, `newWindow(TAB)` | Single tab only; `cy.window()` cannot drive pop-ups | Multiple pages/contexts, but API model differs from WebDriver handles |
| **Relative locators** | Built-in (`above`, `below`, `near`, …) since Selenium 4 | Chain `.parent()`, `.contains()` — different model | `getByRole` + layout; no direct WebDriver relative locators |
| **Frames / iframes** | `switchTo().frame()` across nested framesets | Same-origin only; limited iframe support | `frameLocator()` — good support, Playwright-specific API |
| **Native JS dialogs** | Real OS dialog via `switchTo().alert()` | Stubbed in-browser (`cy.on('window:confirm')`) | `page.on('dialog')` — event handler, not WebDriver alert |
| **Java / Kotlin bindings** | First-class Java API (this project) | Node.js primary; Java exists but community-smaller | Node/Python/Java/C# — Java is supported but not the primary ecosystem |
| **Shared foundation with Appium** | Same WebDriver protocol → mobile, desktop, TV | Separate architecture | Separate architecture; not Appium-compatible |

## Project layout

```
src/test/java/com/nkborba/superpowers/
├── BaseTest.java              # @BeforeMethod / @AfterMethod driver lifecycle
├── ResearchFlowTest.java      # Wikipedia multi-tab flow
└── SeleniumSuperpowersTest.java
```

## License

See [LICENSE](LICENSE).
