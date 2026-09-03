const { test, expect, mockApi } = require("./fixtures")

test.describe("search regression coverage", () => {
  test("offers spell correction for a misspelled query @regression", async ({ mockApi: page }) => {
    await page.goto("/")
    const search = page.getByPlaceholder("Search for laptops...")
    await search.fill("appl")
    await search.press("Enter")
    await expect(page.getByText("Did you mean:")).toBeVisible()
    await expect(page.getByRole("button", { name: /apple \(for \"appl\"\)/ })).toBeVisible()
  })

  test("filters, sorts, and paginates API results @regression", async ({ mockApi: page }) => {
    await page.goto("/")
    await page.getByPlaceholder("Search for laptops...").press("Enter")
    await expect(page.getByText("Showing 1 - 12 of 13 laptops")).toBeVisible()

    const sortSelect = page.getByLabel("Sort by:")
    await sortSelect.selectOption("price-high-low")
    await expect(sortSelect).toHaveValue("price-high-low")
    await page.getByRole("button", { name: "Next →" }).click()
    await expect(page.getByText("Showing 13 - 13 of 13 laptops")).toBeVisible()
    await expect(page.getByRole("button", { name: "← Prev" })).toBeVisible()

    await page.getByRole("button", { name: "← Prev" }).click()
    await page.getByRole("button", { name: "Brands" }).click()
    await page.getByLabel("Apple", { exact: true }).check()
    await expect(page.getByText("Apple Laptop 2")).toBeVisible()
    await expect(page.getByText("Acer Laptop 1", { exact: true })).not.toBeVisible()
  })

  test("compares two products and supports navigation back out @regression", async ({ mockApi: page }) => {
    await page.goto("/")
    await page.getByPlaceholder("Search for laptops...").press("Enter")
    await page.getByText("Apple Laptop 2").click()
    await page.keyboard.press("Escape")
    await page.getByLabel("Compare").nth(0).check()
    await page.getByLabel("Compare").nth(1).check()
    await page.getByRole("button", { name: "Compare (2)" }).click()
    await expect(page.getByRole("heading", { name: "Compare Laptops" })).toBeVisible()
    await page.keyboard.press("Escape")
    await expect(page.getByRole("heading", { name: "Compare Laptops" })).not.toBeVisible()
  })

  test("validates contact form input and submits valid data @regression", async ({ mockApi: page }) => {
    await page.goto("/")
    await page.getByRole("button", { name: "Contact Us" }).click()
    await page.getByPlaceholder("Name").fill("A")
    await page.getByPlaceholder("Email").fill("invalid")
    await page.getByPlaceholder("Phone").fill("123")
    await page.getByRole("button", { name: "Submit" }).click()
    await expect(page.getByText(/Invalid details/)).toBeVisible()

    await page.getByPlaceholder("Name").fill("Alex Smith")
    await page.getByPlaceholder("Email").fill("alex@example.com")
    await page.getByPlaceholder("Phone").fill("416-555-1234")
    await page.getByRole("button", { name: "Submit" }).click()
    await expect(page.getByText("Sent successfully!")).toBeVisible()
  })

  test("handles empty search responses without crashing @regression", async ({ page }) => {
    await mockApi(page, { emptySearch: true })
    await page.goto("/")
    await page.getByPlaceholder("Search for laptops...").press("Enter")
    await expect(page.getByText("No laptops found matching your criteria.")).toBeVisible()
  })

  test("handles API failures without crashing @regression", async ({ page }) => {
    await mockApi(page, { failSearch: true })
    await page.goto("/")
    await page.getByPlaceholder("Search for laptops...").press("Enter")
    await expect(page.getByText("No laptops found matching your criteria.")).toBeVisible()
  })
})
