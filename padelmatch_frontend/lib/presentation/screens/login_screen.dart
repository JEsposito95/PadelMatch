import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'login_provider.dart';

class LoginScreen extends ConsumerStatefulWidget {
  const LoginScreen({super.key});

  @override
  ConsumerState<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends ConsumerState<LoginScreen> {
  final emailController = TextEditingController();
  final passwordController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    final loginState = ref.watch(loginNotifierProvider);

    return Scaffold(
      appBar: AppBar(title: const Text("Iniciar Sesión")),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          children: [
            TextField(
              controller: emailController,
              decoration: const InputDecoration(
                labelText: "Email",
              ),
            ),
            TextField(
              controller: passwordController,
              decoration: const InputDecoration(
                labelText: "Contraseña",
              ),
              obscureText: true,
            ),
            const SizedBox(height: 20),

            loginState.when(
              data: (token) {
                return ElevatedButton(
                  onPressed: () async {
                    final email = emailController.text.trim();
                    final pass = passwordController.text.trim();

                    await ref.read(loginNotifierProvider.notifier).login(email, pass);

                    if (ref.read(loginNotifierProvider).value != null) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text("Inicio de sesión correcto"))
                      );
                    }
                  },
                  child: const Text("Iniciar Sesión"),
                );
              },
              loading: () => const CircularProgressIndicator(),
              error: (err, stack) => Column(
                children: [
                  Text(err.toString(), style: const TextStyle(color: Colors.red)),
                  ElevatedButton(
                    onPressed: () async {
                      final email = emailController.text.trim();
                      final pass = passwordController.text.trim();
                      await ref.read(loginNotifierProvider.notifier).login(email, pass);
                    },
                    child: const Text("Reintentar"),
                  )
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
