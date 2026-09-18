import time

url = "http://localhost:8080/api/v1/auth/login"

headers = {
    "Content-Type": "application/json"
}

payload = {
    "email": "whamoda@gmail.com",
    "password": "74185296"
}

try:
    for i in range(50):
        response = requests.post(url,json=payload,headers=headers,timeout=10)

        print(f"Status Code: {response.status_code}")
        
        print("\nResponse Body:")
        try:
            print(response.json())
        except ValueError:
            print(response.text)
        
        time.sleep(3)

except requests.exceptions.RequestException as e:
    print(f"Request failed: {e}")


input("DONE!!!")