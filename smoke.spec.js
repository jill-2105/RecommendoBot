const { test, expect } = require("./fixtures")

test.describe("critical search workflows", () => {
  test("searches for laptops and opens a product detail modal @smoke", async ({ mockApi }) => {
    await mockApi.goto("/")
    await mockApi.getByPlaceholder("Search for laptops...").fill("apple")
    await mockApi.getByPlaceholder("Search for laptops...").press("Enter")

    await expect(mockApi.getByText("Showing 1 - 12 of 13 laptops")).toBeVisible()
    await expect(mockApi.getByText("Apple Laptop 2")).toBeVisible()
    await mockApi.getByText("Apple Laptop 2").click()
    await expect(mockApi.locator(".modal-content").getByRole("heading", { name: "Apple Laptop 2" })).toBeVisible()
  })

  test("shows autocomplete suggestions and applies a selection @smoke", async ({ mockApi }) => {
    await mockApi.goto("/")
    const search = mockApi.getByPlaceholder("Search for laptops...")
    const autocompleteResponse = mockApi.waitForResponse((response) =>
      response.url().includes("/WebApi") && response.request().postData()?.includes("WordCompletion")
    )
    await search.fill("app")
    await autocompleteResponse
    await expect(mockApi.getByText("apple macbook")).toBeVisible()
    await mockApi.getByText("apple macbook").click()
    await expect(search).toHaveValue("apple macbook")
  })
})
