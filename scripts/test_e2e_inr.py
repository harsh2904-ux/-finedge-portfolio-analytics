import json
import urllib.request

def test_api():
    # 1. Test Login
    login_url = "http://localhost:8080/api/auth/login"
    login_payload = json.dumps({
        "email": "demo@portfolio.com",
        "password": "password123"
    }).encode('utf-8')
    
    req = urllib.request.Request(
        login_url,
        data=login_payload,
        headers={"Content-Type": "application/json"}
    )
    
    with urllib.request.urlopen(req) as resp:
        login_resp = json.loads(resp.read().decode('utf-8'))
        print("Login status:", resp.status)
        data = login_resp.get("data", {})
        token = data.get("accessToken")
        print("User:", data.get("user", {}).get("email"), "Token acquired:", bool(token))

    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }

    # 2. Get Portfolios
    req = urllib.request.Request("http://localhost:8080/api/portfolios", headers=headers)
    with urllib.request.urlopen(req) as resp:
        portfolios = json.loads(resp.read().decode('utf-8'))
        print("\nPortfolios Response:")
        print(json.dumps(portfolios, indent=2))

    # 3. Get Portfolio Valuation (Portfolio 1)
    req = urllib.request.Request("http://localhost:8080/api/portfolios/1/valuation", headers=headers)
    with urllib.request.urlopen(req) as resp:
        valuation = json.loads(resp.read().decode('utf-8'))
        print("\nPortfolio 1 Valuation:")
        print(json.dumps(valuation, indent=2))

    # 4. Get Latest Market Price for Indian Asset (RELIANCE)
    req = urllib.request.Request("http://localhost:8080/api/market/RELIANCE/latest", headers=headers)
    with urllib.request.urlopen(req) as resp:
        price = json.loads(resp.read().decode('utf-8'))
        print("\nLatest RELIANCE Market Price:")
        print(json.dumps(price, indent=2))

    # 5. Check frontend proxy / index
    req = urllib.request.Request("http://localhost:3000/")
    with urllib.request.urlopen(req) as resp:
        print("\nFrontend HTTP status:", resp.status)

if __name__ == "__main__":
    test_api()
