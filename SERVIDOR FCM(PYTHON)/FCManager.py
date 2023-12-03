import firebase_admin
from firebase_admin import credentials, db
from pyfcm import FCMNotification
from datetime import datetime, timedelta
import time
import logging

# Configurando os logs
logging.basicConfig(level=logging.INFO)

# Inicializa o Firebase Admin SDK
cred = credentials.Certificate("C:\\Users\\Pichau\\Desktop\\FirebaseMensagem\\serviceAccountKey.json")
firebase_admin.initialize_app(cred, {"databaseURL": "https://dosedaily-4dd77-default-rtdb.firebaseio.com/"})

# Referência ao nó "Medicamento" no RealTimeDatabase
ref = db.reference("Medicamento")

# Configuração do Firebase Cloud Messaging (FCM)
push_service = FCMNotification(api_key="AAAARv0J7P4:APA91bGg3guaSbtTep6fWeMDRapv9TxGLCirMKOfB16cpsDVpp_4FbP89wTtVoiN3mHNUKd4iHtXBivlFSs3LnxZtkDu5RsfLpG4IeThBOMPDR8eIgECrK9CAaxaQGWEsakcYAbY3BwN")
while True:
    # Obtendo a data e hora atual no formato "ano-mes-dia hora:minuto"
    current_time = datetime.now().strftime("%Y-%m-%d %H:%M")
    logging.info(f"Tempo atual: {current_time}")

    # Consulta o RealTimeDatabase para obter os medicamentos agendados
    medicamentos = ref.get()

    if medicamentos:
        for key, medicamento in medicamentos.items():
            if all(field in medicamento for field in ["corpo", "deviceToken", "tempoNotificacao", "titulo", "frequencia"]):
                titulo = medicamento["titulo"]
                corpo = medicamento["corpo"]
                device_token = medicamento["deviceToken"]
                tempo_notificacao = medicamento["tempoNotificacao"]
                frequencia = medicamento["frequencia"]

                # Verifica se o tempo de notificação coincide com o momento atual
                if tempo_notificacao == current_time:
                    # Envia os dados para o dispositivo usando o token do dispositivo
                    message = {"titulo": titulo, "corpo": corpo}
                    result = push_service.single_device_data_message(registration_id=device_token, data_message=message)
                    logging.info(f"Resultado do envio de dados para {device_token}: {result}")

                    # Verifica a frequência e adiciona um novo medicamento com tempo_notificacao ajustado
                    if frequencia == "Diariamente":
                        novo_tempo_notificacao = (datetime.strptime(tempo_notificacao, "%Y-%m-%d %H:%M") + timedelta(days=1)).strftime("%Y-%m-%d %H:%M")
                    elif frequencia == "Semanalmente":
                        novo_tempo_notificacao = (datetime.strptime(tempo_notificacao, "%Y-%m-%d %H:%M") + timedelta(weeks=1)).strftime("%Y-%m-%d %H:%M")
                    elif frequencia == "Mensalmente":
                        novo_tempo_notificacao = (datetime.strptime(tempo_notificacao, "%Y-%m-%d %H:%M") + relativedelta(months=1)).strftime("%Y-%m-%d %H:%M")
                    else:
                        novo_tempo_notificacao = None  # Se a frequência não for reconhecida, não faz nada

                    # Adiciona um novo medicamento se a frequência for válida
                    if novo_tempo_notificacao:
                        novo_medicamento = {
                            "titulo": titulo,
                            "corpo": corpo,
                            "deviceToken": device_token,
                            "tempoNotificacao": novo_tempo_notificacao,
                            "frequencia": frequencia
                        }
                        ref.push(novo_medicamento)

                    # Exclui o medicamento do RealTimeDatabase após o envio bem-sucedido ou se não houver frequência válida
                    ref.child(key).delete()
                    logging.info(f"Medicamento {key} excluído do RealTimeDatabase após envio bem-sucedido ou sem frequência válida.")

                else:
                    logging.info(f"Tempo de notificação do medicamento {key} não coincide com o momento atual. Ignorando.")
            else:
                logging.info(f"Medicamento {key} não possui todos os campos necessários. Ignorando.")
    else:
        logging.info("Nenhum medicamento encontrado para envio de dados.")

    # Espera por 1 minuto antes de fazer a próxima verificação
    time.sleep(60)
