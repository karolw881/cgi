#!/usr/bin/env python3
import cgi
import socket
import html

# Konfiguracja serwera
SERVER_HOST = "localhost"
SERVER_PORT = 6196

def main():
    print("Content-Type: text/html\n")
    form = cgi.FieldStorage()

    # Pobranie danych z formularza
    user_id = form.getvalue("user_id", "").strip()
    message = form.getvalue("message", "").strip()

    # Walidacja danych
    if not user_id or not message:
        print("<html><body>")
        print("<h1>Błąd</h1>")
        print("<p>Zarówno ID użytkownika, jak i wiadomość są wymagane!</p>")
        print("</body></html>")
        return

    try:
        # Połączenie z serwerem
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((SERVER_HOST, SERVER_PORT))

            # Wiadomość w formacie: "userID: message" (przyjmuje wielolinie)
            full_message = f"{user_id}: {message}\n\n"  # Podwójne \n oznacza koniec wiadomości
            s.sendall(full_message.encode('utf-8'))  # Wysyłamy wiadomość zakodowaną w UTF-8

            # Odbiór odpowiedzi
            response = s.recv(1024).decode('utf-8').strip()
        
        # Wyświetlenie odpowiedzi w przeglądarce
        print("<html><body>")
        print("<h2>Wiadomość wysłana</h2>")
        print(f"<p>Odpowiedź serwera: {html.escape(response)}</p>")
        print("<a href='socket_form.html'>Wyślij kolejną wiadomość</a>")
        print("</body></html>")
    
    except Exception as e:
        # Obsługa błędów
        print("<html><body>")
        print("<h1>Błąd</h1>")
        print(f"<p>{html.escape(str(e))}</p>")
        print("</body></html>")

if __name__ == "__main__":
    main()
