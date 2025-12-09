import 'package:dio/dio.dart';
import '../../core/api/dio_client.dart';

class AuthService {
  final DioClient _client = DioClient();

  Future<String> login(String email, String password) async {
    try {
      final response = await _client.dio.post(
        "/auth/login",
        data: {
          "email": email,
          "password": password,
        },
      );

      return response.data["token"]; // OK
    } on DioException catch (e) {
      throw Exception(e.response?.data["message"] ?? "Error inesperado");
    }
  }
}
