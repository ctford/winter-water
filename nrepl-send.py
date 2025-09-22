#!/usr/bin/env python3
"""
Fast nREPL client for shell scripts - based on vim-fireplace approach
Sends bencode messages directly to nREPL socket for fast response
"""
import socket
import sys
import uuid
import time

def bencode(data):
    """Encode data in bencode format"""
    if isinstance(data, dict):
        result = b'd'
        for key in sorted(data.keys()):
            result += bencode(key) + bencode(data[key])
        result += b'e'
        return result
    elif isinstance(data, str):
        data_bytes = data.encode('utf-8')
        return str(len(data_bytes)).encode('utf-8') + b':' + data_bytes
    elif isinstance(data, int):
        return b'i' + str(data).encode('utf-8') + b'e'
    elif isinstance(data, list):
        result = b'l'
        for item in data:
            result += bencode(item)
        result += b'e'
        return result
    else:
        raise TypeError(f"Cannot bencode {type(data)}")

def bdecode(sock):
    """Decode bencode data from socket"""
    def read_bytes(n):
        data = b''
        while len(data) < n:
            chunk = sock.recv(n - len(data))
            if not chunk:
                raise ConnectionError("Connection closed")
            data += chunk
        return data
    
    def read_char():
        return read_bytes(1)
    
    def decode_item():
        char = read_char()
        if char == b'd':
            # Dictionary
            result = {}
            while True:
                char = read_char()
                if char == b'e':
                    return result
                # Put char back by reading the key starting with this char
                key = decode_string_with_first_char(char)
                value = decode_item()
                result[key] = value
        elif char == b'l':
            # List
            result = []
            while True:
                char = read_char()
                if char == b'e':
                    return result
                # Put char back
                item = decode_string_with_first_char(char) if char.isdigit() else decode_item_with_first_char(char)
                result.append(item)
        elif char == b'i':
            # Integer
            num_str = b''
            while True:
                char = read_char()
                if char == b'e':
                    return int(num_str.decode('utf-8'))
                num_str += char
        elif char.isdigit():
            return decode_string_with_first_char(char)
        else:
            raise ValueError(f"Unknown bencode type: {char}")
    
    def decode_string_with_first_char(first_char):
        length_str = first_char
        while True:
            char = read_char()
            if char == b':':
                break
            length_str += char
        length = int(length_str.decode('utf-8'))
        return read_bytes(length).decode('utf-8')
    
    def decode_item_with_first_char(first_char):
        if first_char == b'd':
            result = {}
            while True:
                char = read_char()
                if char == b'e':
                    return result
                key = decode_string_with_first_char(char)
                value = decode_item()
                result[key] = value
        elif first_char == b'i':
            num_str = b''
            while True:
                char = read_char()
                if char == b'e':
                    return int(num_str.decode('utf-8'))
                num_str += char
        else:
            raise ValueError(f"Unknown bencode type: {first_char}")
    
    return decode_item()

def send_nrepl_message(host, port, code):
    """Send code to nREPL and return response"""
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(10)
        sock.connect((host, port))
        
        # Create nREPL message
        message = {
            'op': 'eval',
            'code': code,
            'id': str(uuid.uuid4())
        }
        
        # Send bencode message
        encoded = bencode(message)
        sock.sendall(encoded)
        
        # Read responses until done
        responses = []
        while True:
            try:
                response = bdecode(sock)
                responses.append(response)
                
                # Check if we're done
                if 'status' in response and 'done' in response['status']:
                    break
                    
            except Exception as e:
                break
        
        sock.close()
        return responses
        
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        return []

def main():
    if len(sys.argv) < 2:
        print("Usage: nrepl-send.py <code> [host] [port]", file=sys.stderr)
        sys.exit(1)
    
    code = sys.argv[1]
    host = sys.argv[2] if len(sys.argv) > 2 else '127.0.0.1'
    port = int(sys.argv[3]) if len(sys.argv) > 3 else 7888
    
    responses = send_nrepl_message(host, port, code)
    
    # Print any values or errors
    for response in responses:
        if 'value' in response:
            print(response['value'])
        if 'err' in response:
            print(response['err'], file=sys.stderr)
        if 'out' in response:
            print(response['out'], end='')

if __name__ == '__main__':
    main()