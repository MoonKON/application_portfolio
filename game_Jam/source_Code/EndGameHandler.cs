using System.Collections;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class EndGameHandler : MonoBehaviour
{
    [SerializeField] private Image fadeImage; // 用于黑屏淡出的Image
    [SerializeField] private float fadeDuration = 2f; // 淡出持续时间
    [SerializeField] private string nextSceneName; // 需要加载的下一个场景名称

    private bool isGameEnding = false;

    // 碰撞检测
    private void OnTriggerEnter2D(Collider2D other)
    {
        // 如果检测到的组件有 "sea" 标签，并且游戏尚未结束
        if (other.CompareTag("Sea") && !isGameEnding)
        {
            Debug.Log("Sea touch!");
            isGameEnding = true; // 防止多次触发
            StartCoroutine(EndGame());
        }
    }

    // 游戏结束协程
    private IEnumerator EndGame()
    {
        // 暂停游戏
        Time.timeScale = 0f;

        // 开始黑屏淡出
        yield return StartCoroutine(FadeOut());

        // 等待淡出完成后，加载下一个场景
        SceneManager.LoadScene(nextSceneName);
    }

    // 实现黑屏淡出效果的协程
    private IEnumerator FadeOut()
    {
        Color fadeColor = fadeImage.color;
        float fadeSpeed = 1f / fadeDuration;
        float fadeAmount = 0f;

        // 循环改变Image的Alpha值，实现淡出效果
        while (fadeAmount < 1f)
        {
            fadeAmount += fadeSpeed * Time.unscaledDeltaTime; // 使用unscaledDeltaTime以适应Time.timeScale为0
            fadeColor.a = Mathf.Clamp01(fadeAmount);
            fadeImage.color = fadeColor;
            yield return null;
        }
    }
}

