import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'register_provider.dart';

class RegisterScreen extends ConsumerWidget {
  const RegisterScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final state = ref.watch(registerNotifierProvider);

    // Controllers
    final nameCtrl = TextEditingController();
    final emailCtrl = TextEditingController();
    final passCtrl = TextEditingController();
    final pass2Ctrl = TextEditingController();

    return Scaffold(
      appBar: AppBar(title: const Text("Registrarse")),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            TextField(
              controller: nameCtrl,
              decoration: const InputDecoration(labelText: "Nombre"),
            ),
            TextField(
              controller: emailCtrl,
              decoration: const InputDecoration(labelText: "Email"),
            ),
            TextField(
              controller: passCtrl,
              decoration: const InputDecoration(labelText: "Contraseña"),
              obscureText: true,
            ),
            TextField(
              controller: pass2Ctrl,
              decoration: const InputDecoration(labelText: "Repetir contraseña"),
              obscureText: true,
            ),
            const SizedBox(height: 20),

            state.when(
              data: (_) => ElevatedButton(
                onPressed: () async {
                  final name = nameCtrl.text.trim();
                  final email = emailCtrl.text.trim();
                  final pass1 = passCtrl.text.trim();
                  final pass2 = pass2Ctrl.text.trim();

                  if (pass1 != pass2) {
                    ScaffoldMessenger.of(context)
                      .showSnackBar(const SnackBar(content: Text("Las contraseñas no coinciden")));
                    return;
                  }

                  await ref.read(registerNotifierProvider.notifier)
                    .register(name, email, pass1, "");

                  if (ref.read(registerNotifierProvider).value != null) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text("Usuario registrado con éxito"))
                    );
                    Navigator.pop(context);
                  }
                },
                child: const Text("Registrarme"),
              ),
              loading: () => const CircularProgressIndicator(),
              error: (err, st) => Column(
                children: [
                  Text(err.toString(), style: const TextStyle(color: Colors.red)),
                  ElevatedButton(
                    onPressed: () {
                      final name = nameCtrl.text.trim();
                      final email = emailCtrl.text.trim();
                      final pass = passCtrl.text.trim();
                      ref.read(registerNotifierProvider.notifier).register(name, email, pass, "");
                    },
                    child: const Text("Reintentar"),
                  ),
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}
