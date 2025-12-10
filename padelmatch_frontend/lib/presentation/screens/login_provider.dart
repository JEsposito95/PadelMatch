import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/services/auth_service.dart';

final loginNotifierProvider =
    AsyncNotifierProvider<LoginNotifier, String?>(LoginNotifier.new);

class LoginNotifier extends AsyncNotifier<String?> {
  @override
  Future<String?> build() async => null;

  Future<void> login(String email, String password) async {
    state = const AsyncValue.loading();
    try {
      final auth = ref.read(authServiceProvider);
      final token = await auth.login(email, password);
      state = AsyncValue.data(token);
    } catch (e, st) {
      state = AsyncValue.error(e.toString(), st);
    }
  }
}
