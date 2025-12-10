import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class AuthService {
  final Dio _dio = Dio(
    BaseOptions(
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
      },
    ),
  );

  Future<String> login(String email, String password) async {
    final url = "${dotenv.env['API_URL']}/api/auth/login";

    final response = await _dio.post(
      url,
      data: {
        "email": email,
        "password": password,
      },
      options: Options(
        contentType: Headers.jsonContentType,
      ),
    );

    return response.data["token"];
  }

  Future<String> register({
    required String name,
    required String email,
    required String password,
  }) async {
    final url = "${dotenv.env['API_URL']}/api/auth/register";

    final response = await _dio.post(
      url,
      data: {
        "name": name,
        "email": email,
        "password": password,
      },
      options: Options(
        contentType: Headers.jsonContentType,
      ),
    );

    return response.data["token"];
  }
}

final authServiceProvider = Provider<AuthService>((ref) {
  return AuthService();
});
