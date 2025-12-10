import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../services/auth_service.dart';

final registerNotifierProvider =
    AsyncNotifierProvider<RegisterNotifier, String?>(RegisterNotifier.new);

class RegisterNotifier extends AsyncNotifier<String?> {
  @override
  Future<String?> build() async => null;

  Future<void> register(String name, String email, String password) async {
    state = const AsyncValue.loading();
    try {
      final auth = ref.read(authServiceProvider);
      final token = await auth.register(
        name: name,
        email: email,
        password: password,
      );
      state = AsyncValue.data(token);
    } catch (e, st) {
      state = AsyncValue.error(e.toString(), st);
    }
  }
}
