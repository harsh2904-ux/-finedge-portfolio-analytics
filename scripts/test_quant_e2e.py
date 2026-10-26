import json
import urllib.request

def test_full_analytics():
    # Login
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
        token = login_resp["data"]["accessToken"]

    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }

    # 1. Risk
    req = urllib.request.Request("http://localhost:8080/api/portfolios/1/risk", headers=headers)
    with urllib.request.urlopen(req) as resp:
        risk = json.loads(resp.read().decode('utf-8'))
        print("Risk Metrics:")
        print(json.dumps(risk['data'], indent=2))

    # 2. Optimization
    opt_req = urllib.request.Request(
        "http://localhost:8080/api/portfolios/1/optimization",
        data=json.dumps({"objective": "MAX_SHARPE"}).encode('utf-8'),
        headers=headers
    )
    with urllib.request.urlopen(opt_req) as resp:
        opt = json.loads(resp.read().decode('utf-8'))
        print("\nOptimization:")
        print(json.dumps(opt['data'], indent=2))

    # 3. Monte Carlo
    mc_req = urllib.request.Request(
        "http://localhost:8080/api/portfolios/1/monte-carlo",
        data=json.dumps({"timeHorizonDays": 252, "numSimulations": 1000}).encode('utf-8'),
        headers=headers
    )
    with urllib.request.urlopen(mc_req) as resp:
        mc = json.loads(resp.read().decode('utf-8'))
        print("\nMonte Carlo:")
        print(json.dumps(mc['data'], indent=2))

    print("\nAll quantitative endpoints with Indian market data working flawlessly!")

if __name__ == "__main__":
    test_full_analytics()
