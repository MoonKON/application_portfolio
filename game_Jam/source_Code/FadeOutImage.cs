using UnityEngine;
using UnityEngine.UI; 
using System.Collections;

public class FadeOutImage : MonoBehaviour
{
    public CanvasGroup canvasGroup; 
    public float fadeDuration = 2f; 

    void Start()
    {
       
        StartCoroutine(FadeOut());
    }

    private IEnumerator FadeOut()
    {
       
        yield return new WaitForSeconds(1f);

   
        float startAlpha = canvasGroup.alpha;
        float time = 0f;

        while (time < fadeDuration)
        {
            time += Time.deltaTime;
            canvasGroup.alpha = Mathf.Lerp(startAlpha, 0, time / fadeDuration);
            yield return null; 
        }

 
        canvasGroup.alpha = 0;
    }
}
